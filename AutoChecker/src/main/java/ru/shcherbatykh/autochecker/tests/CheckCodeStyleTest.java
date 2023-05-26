package ru.shcherbatykh.autochecker.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeParameters;
import com.github.javaparser.ast.type.TypeParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.autochecker.ast.AstUtils;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;
import ru.shcherbatykh.autochecker.model.CodeTestResult;
import ru.shcherbatykh.autochecker.model.CodeTestType;
import ru.shcherbatykh.autochecker.model.Status;
import ru.shcherbatykh.autochecker.rules.model.RuleViolation;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component("checkCodeStyle")
@Scope("prototype")
public class CheckCodeStyleTest implements CodeTest {
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-z_]+(\\.[a-z_][a-z0-9_]*)*$");
    private static final Pattern ABSTRACT_CLASS_PATTERN = Pattern.compile("^Abstract[A-Z][a-zA-Z0-9]*$");
    private static final Pattern CLASS_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    private static final Pattern INTERFACE_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    private static final Pattern CONST_PATTERN = Pattern.compile("^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$");
    private static final Pattern STATIC_NON_FINAL_FIELD_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern FIELD_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern LOCAL_CONST_PATTERN = Pattern.compile("^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$");
    private static final Pattern LOCAL_VARIABLE_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern METHOD_PARAMETER_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern GENERIC_PATTERN = Pattern.compile("^[A-Z]$");

    private final List<RuleViolation> ruleViolations = new ArrayList<>();

    @Override
    public List<CodeTestResult> launchTest(CodeCheckContext codeCheckContext) {
        List<CompilationUnit> compilationUnits = codeCheckContext.getCompilationUnits();
        compilationUnits.forEach(this::validateCompilationUnit);
        if (ruleViolations.isEmpty()) {
            return Collections.singletonList(CodeTestResult.OK_CHECKSTYLE_RESULT);
        } else {
            return Collections.singletonList(CodeTestResult.builder()
                    .status(Status.NOK)
                    .type(CodeTestType.CHECKSTYLE)
                    .result(createRuleViolationsNode(ruleViolations))
                    .build());
        }
    }

    private void validateCompilationUnit(CompilationUnit compilationUnit) {
        compilationUnit.getPackageDeclaration()
                .ifPresent(declaration -> validatePackageName(declaration.getNameAsString()));
        compilationUnit.findAll(TypeDeclaration.class)
                .forEach(this::validateTypeDeclaration);
    }

    private void validateTypeDeclaration(TypeDeclaration<?> typeDeclaration) {
        if (typeDeclaration.isClassOrInterfaceDeclaration()) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = typeDeclaration.asClassOrInterfaceDeclaration();
            if (classOrInterfaceDeclaration.isInterface()) {
                validateInterface(classOrInterfaceDeclaration);
            } else {
                validateClass(classOrInterfaceDeclaration, classOrInterfaceDeclaration.isLocalClassDeclaration()
                        && classOrInterfaceDeclaration.isAbstract());
            }
        } else if (typeDeclaration.isEnumDeclaration()) {
            EnumDeclaration enumDeclaration = typeDeclaration.asEnumDeclaration();
            validateEnum(enumDeclaration);
        } else if (typeDeclaration.isAnnotationDeclaration()) {
            AnnotationDeclaration annotationDeclaration = typeDeclaration.asAnnotationDeclaration();
            validateClass(annotationDeclaration, false);
        } else if (typeDeclaration.isRecordDeclaration()) {
            RecordDeclaration recordDeclaration = typeDeclaration.asRecordDeclaration();
            validateRecord(recordDeclaration);
        }

        if (typeDeclaration instanceof NodeWithTypeParameters<?> nodeWithTypeParameters) {
            NodeList<TypeParameter> typeParameters = nodeWithTypeParameters.getTypeParameters();
            typeParameters.forEach(typeParameter -> validateTypeParameter(typeDeclaration, typeParameter));
        }
    }

    private void validateInterface(ClassOrInterfaceDeclaration interfaceDeclaration) {
        validateInterfaceName(interfaceDeclaration);

        List<VariableDeclarator> variableDeclarators = interfaceDeclaration.getFields().stream()
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        variableDeclarators.forEach(variableDeclarator -> validateInterfaceFieldName(interfaceDeclaration, variableDeclarator));

        List<MethodDeclaration> methods = interfaceDeclaration.getMethods();
        methods.forEach(methodDeclaration -> validateMethod(interfaceDeclaration, methodDeclaration));
    }

    private void validateClass(TypeDeclaration<?> classDeclaration, boolean isAbstract) {
        if (isAbstract) {
            validateAbstractClassName(classDeclaration);
        } else {
            validateClassName(classDeclaration);
        }

        List<VariableDeclarator> staticFinalVariableDeclarators = classDeclaration.getFields().stream()
                .filter(fieldDeclaration -> fieldDeclaration.isStatic() && fieldDeclaration.isFinal())
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        staticFinalVariableDeclarators.forEach(variableDeclarator ->
                validateClassConstantName(classDeclaration, variableDeclarator));

        List<VariableDeclarator> staticNonFinalVariableDeclarators = classDeclaration.getFields().stream()
                .filter(fieldDeclaration -> fieldDeclaration.isStatic() && !fieldDeclaration.isFinal())
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        staticNonFinalVariableDeclarators.forEach(variableDeclarator ->
                validateClassStaticNonFinalFieldName(classDeclaration, variableDeclarator));

        List<VariableDeclarator> variableDeclarators = classDeclaration.getFields().stream()
                .filter(fieldDeclaration -> !fieldDeclaration.isStatic())
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        variableDeclarators.forEach(variableDeclarator ->
                validateClassVariableName(classDeclaration, variableDeclarator));

        List<MethodDeclaration> methods = classDeclaration.getMethods();
        methods.forEach(methodDeclaration -> validateMethod(classDeclaration, methodDeclaration));
    }

    private void validateEnum(EnumDeclaration enumDeclaration) {
        validateClassName(enumDeclaration);

        NodeList<EnumConstantDeclaration> entries = enumDeclaration.getEntries();
        entries.forEach(enumConstantDeclaration -> validateEnumConstant(enumDeclaration, enumConstantDeclaration));

        List<MethodDeclaration> methods = enumDeclaration.getMethods();
        methods.forEach(methodDeclaration -> validateMethod(enumDeclaration, methodDeclaration));
    }

    private void validateRecord(RecordDeclaration recordDeclaration) {
        validateClassName(recordDeclaration);

        NodeList<Parameter> parameters = recordDeclaration.getParameters();
        parameters.forEach(parameter ->
                validateRecordParameterName(recordDeclaration, parameter));

        List<VariableDeclarator> staticFinalVariableDeclarators = recordDeclaration.getFields().stream()
                .filter(fieldDeclaration -> fieldDeclaration.isStatic() && fieldDeclaration.isFinal())
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        staticFinalVariableDeclarators.forEach(variableDeclarator ->
                validateClassConstantName(recordDeclaration, variableDeclarator));

        List<VariableDeclarator> staticNonFinalVariableDeclarators = recordDeclaration.getFields().stream()
                .filter(fieldDeclaration -> fieldDeclaration.isStatic() && !fieldDeclaration.isFinal())
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        staticNonFinalVariableDeclarators.forEach(variableDeclarator ->
                validateClassStaticNonFinalFieldName(recordDeclaration, variableDeclarator));

        List<VariableDeclarator> variableDeclarators = recordDeclaration.getFields().stream()
                .filter(fieldDeclaration -> !fieldDeclaration.isStatic())
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        variableDeclarators.forEach(variableDeclarator ->
                validateClassVariableName(recordDeclaration, variableDeclarator));

        List<MethodDeclaration> methods = recordDeclaration.getMethods();
        methods.forEach(methodDeclaration -> validateMethod(recordDeclaration, methodDeclaration));
    }

    private void validatePackageName(String name) {
        if (!PACKAGE_PATTERN.matcher(name).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени пакета",
                    MessageFormat.format("Наименование пакета ''{0}'' не соответствует шаблону ''{1}''.",
                            name, PACKAGE_PATTERN.pattern())));
        }
    }

    private void validateInterfaceName(TypeDeclaration<?> typeDeclaration) {
        if (!INTERFACE_PATTERN.matcher(typeDeclaration.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени интерфейса",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя интерфейса ''{0}'' не соответствует шаблону ''{1}''.",
                            typeDeclaration.getNameAsString(), INTERFACE_PATTERN.pattern())));
        }
    }

    private void validateAbstractClassName(TypeDeclaration<?> typeDeclaration) {
        if (!ABSTRACT_CLASS_PATTERN.matcher(typeDeclaration.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени абстрактного класса",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя абстрактного класса ''{0}'' не соответствует шаблону ''{1}''.",
                            typeDeclaration.getNameAsString(), ABSTRACT_CLASS_PATTERN.pattern())));
        }
    }

    private void validateClassName(TypeDeclaration<?> typeDeclaration) {
        if (!CLASS_PATTERN.matcher(typeDeclaration.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени класса",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя класса ''{0}'' не соответствует шаблону ''{1}''.",
                            typeDeclaration.getNameAsString(), CLASS_PATTERN.pattern())));
        }
    }

    private void validateInterfaceFieldName(TypeDeclaration<?> typeDeclaration,
                                            VariableDeclarator variableDeclarator) {
        if (!CONST_PATTERN.matcher(variableDeclarator.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени поля интерфейса",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя поля ''{0}'' не соответствует шаблону ''{1}''.",
                            variableDeclarator.getNameAsString(), CONST_PATTERN.pattern())));
        }
    }

    private void validateClassConstantName(TypeDeclaration<?> typeDeclaration, VariableDeclarator variableDeclarator) {
        if (!CONST_PATTERN.matcher(variableDeclarator.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени константы класса",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя константы ''{0}'' не соответствует шаблону ''{1}''.",
                            variableDeclarator.getNameAsString(), CONST_PATTERN.pattern())));
        }
    }

    private void validateClassStaticNonFinalFieldName(TypeDeclaration<?> typeDeclaration,
                                                      VariableDeclarator variableDeclarator) {
        if (!STATIC_NON_FINAL_FIELD_PATTERN.matcher(variableDeclarator.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени статической переменной класса",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя статической переменной ''{0}'' не соответствует шаблону ''{1}''.",
                            variableDeclarator.getNameAsString(), STATIC_NON_FINAL_FIELD_PATTERN.pattern())));
        }
    }

    private void validateClassVariableName(TypeDeclaration<?> typeDeclaration, VariableDeclarator variableDeclarator) {
        if (!FIELD_PATTERN.matcher(variableDeclarator.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени переменной класса",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя переменной ''{0}'' не соответствует шаблону ''{1}''.",
                            variableDeclarator.getNameAsString(), FIELD_PATTERN.pattern())));
        }
    }

    private void validateEnumConstant(EnumDeclaration enumDeclaration, EnumConstantDeclaration enumConstantDeclaration) {
        if (!CONST_PATTERN.matcher(enumConstantDeclaration.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени константы перечисления",
                    AstUtils.getFullyQualifiedName(enumDeclaration),
                    MessageFormat.format("Имя константы ''{0}'' не соответствует шаблону ''{1}''.",
                            enumConstantDeclaration.getNameAsString(), CONST_PATTERN.pattern())));
        }
    }

    private void validateRecordParameterName(RecordDeclaration recordDeclaration, Parameter parameter) {
        if (!METHOD_PARAMETER_PATTERN.matcher(parameter.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени параметра записи",
                    AstUtils.getFullyQualifiedName(recordDeclaration),
                    MessageFormat.format("Имя параметра ''{0}'' не соответствует шаблону ''{1}''.",
                            parameter.getNameAsString(), METHOD_PARAMETER_PATTERN.pattern())));
        }
    }

    private void validateMethod(TypeDeclaration<?> typeDeclaration, MethodDeclaration methodDeclaration) {
        validateMethodName(typeDeclaration, methodDeclaration);
        methodDeclaration.getParameters()
                .forEach(parameter -> validateMethodParameterName(typeDeclaration, methodDeclaration, parameter));
        if (methodDeclaration.getBody().isPresent()) {
            methodDeclaration.getBody().get().findAll(VariableDeclarationExpr.class).stream()
                    .filter(VariableDeclarationExpr::isFinal)
                    .map(VariableDeclarationExpr::getVariables)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .forEach(variableDeclarator ->
                            validateVariableName(typeDeclaration, methodDeclaration, variableDeclarator, true));
            methodDeclaration.getBody().get().findAll(VariableDeclarationExpr.class).stream()
                    .filter(variableDeclarationExpr -> !variableDeclarationExpr.isFinal())
                    .map(VariableDeclarationExpr::getVariables)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .forEach(variableDeclarator ->
                            validateVariableName(typeDeclaration, methodDeclaration, variableDeclarator, false));
        }
        NodeList<TypeParameter> typeParameters = methodDeclaration.getTypeParameters();
        typeParameters.forEach(typeParameter -> validateMethodTypeParameter(typeDeclaration, methodDeclaration, typeParameter));
    }

    private void validateMethodName(TypeDeclaration<?> typeDeclaration, MethodDeclaration methodDeclaration) {
        if (!METHOD_PATTERN.matcher(methodDeclaration.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени метода",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя метода ''{0}'' не соответствует шаблону ''{1}''.",
                            methodDeclaration.getDeclarationAsString(false, false, false),
                            CONST_PATTERN.pattern())));
        }
    }

    private void validateMethodParameterName(TypeDeclaration<?> typeDeclaration,
                                             MethodDeclaration methodDeclaration,
                                             Parameter parameter) {
        if (!METHOD_PARAMETER_PATTERN.matcher(parameter.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени параметра метода",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя параметра ''{0}'' метода ''{1}'' не соответствует шаблону ''{2}''.",
                            parameter.getNameAsString(),
                            methodDeclaration.getDeclarationAsString(false, false, false),
                            METHOD_PARAMETER_PATTERN.pattern())));
        }
    }

    private void validateVariableName(TypeDeclaration<?> typeDeclaration,
                                      MethodDeclaration methodDeclaration,
                                      VariableDeclarator variableDeclarator,
                                      boolean isFinal) {
        if (isFinal && !LOCAL_CONST_PATTERN.matcher(variableDeclarator.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени локальной константы",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя локальной константы ''{0}'' метода ''{1}'' не соответствует шаблону ''{2}''.",
                            variableDeclarator.getNameAsString(),
                            methodDeclaration.getDeclarationAsString(false, false, false),
                            METHOD_PARAMETER_PATTERN.pattern())));
        } else if (!isFinal && !LOCAL_VARIABLE_PATTERN.matcher(variableDeclarator.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени локальной переменной",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя локальной переменной ''{0}'' метода ''{1}'' не соответствует шаблону ''{2}''.",
                            variableDeclarator.getNameAsString(),
                            methodDeclaration.getDeclarationAsString(false, false, false),
                            LOCAL_VARIABLE_PATTERN.pattern())));

        }
    }

    private void validateTypeParameter(TypeDeclaration<?> typeDeclaration, TypeParameter typeParameter) {
        if (!GENERIC_PATTERN.matcher(typeParameter.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени параметризации типа",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя параметризации ''{0}'' не соответствует шаблону ''{1}''.",
                            typeParameter.getNameAsString(),
                            GENERIC_PATTERN.pattern())));
        }
    }

    private void validateMethodTypeParameter(TypeDeclaration<?> typeDeclaration,
                                             MethodDeclaration methodDeclaration,
                                             TypeParameter typeParameter) {
        if (!GENERIC_PATTERN.matcher(typeParameter.getNameAsString()).matches()) {
            ruleViolations.add(new RuleViolation("Проверка имени параметризации метода",
                    AstUtils.getFullyQualifiedName(typeDeclaration),
                    MessageFormat.format("Имя параметризации ''{0}'' метода ''{1}'' не соответствует шаблону ''{2}''.",
                            typeParameter.getNameAsString(),
                            methodDeclaration.getDeclarationAsString(false, false, false),
                            GENERIC_PATTERN.pattern())));
        }
    }
}
