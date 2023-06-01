package ru.shcherbatykh.codetester.rules;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.resolution.types.ResolvedType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.codetester.ast.AstUtils;
import ru.shcherbatykh.codetester.ast.Predicates;
import ru.shcherbatykh.codetester.model.CodeCheckContext;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class ClassVariableTypesRule extends AbstractRule<ClassOrInterfaceDeclaration> {
    public static final String ALL_VAR = "ALL";
    public static final String EXPECTED_AMOUNT_OF_VARIABLES = "EXPECTED_AMOUNT_OF_VARIABLES";
    public static final String VARIABLE_2_TYPE_MAP = "VARIABLE_TO_TYPE_MAP";

    public static final String NUMBER_TYPE = "t_number";
    public static final String INTEGER_TYPE = "t_integer";
    public static final String REAL_TYPE = "t_real";
    public static final String ANY_TYPE = "t_any";
    public static final String INT_OR_LONG_TYPE = "t_int_or_long";

    private static final Map<String, String> TYPE_2_NAME_MAP = Map.of(
            NUMBER_TYPE, "число",
            INTEGER_TYPE, "целое число",
            REAL_TYPE, "действительное число",
            ANY_TYPE, "любой",
            INT_OR_LONG_TYPE, "int или long"
    );

    @Override
    @SuppressWarnings("unchecked")
    public void applyRule(ClassOrInterfaceDeclaration node, CodeCheckContext codeCheckContext) {
        Map<String, Object> inputContext = getInputContext();
        List<VariableDeclarator> variables = node.findAll(FieldDeclaration.class).stream()
                .map(FieldDeclaration::getVariables)
                .flatMap(Collection::stream)
                .toList();
        int expectedAmountOfVariables = (int) inputContext.get(EXPECTED_AMOUNT_OF_VARIABLES);
        if (variables.size() != expectedAmountOfVariables) {
            addViolation(AstUtils.getFullyQualifiedName(node),
                    getWrongAmountOfVariablesDescription(expectedAmountOfVariables, variables.size()));
            return;
        }

        Map<String, String> varToTypeMap = (Map<String, String>) inputContext.get(VARIABLE_2_TYPE_MAP);
        if (varToTypeMap.containsKey(ALL_VAR)) {
            String expectedType = varToTypeMap.get(ALL_VAR);
            Predicate<ResolvedType> predicateForExpectedType = getPredicateForType(expectedType);
            List<VariableDeclarator> unsatisfiedVariables = variables.stream()
                    .filter(variableDeclarator -> {
                        ResolvedType resolvedType = codeCheckContext.getJavaParserFacade().convertToUsage(variableDeclarator.getType());
                        return predicateForExpectedType.negate().test(resolvedType);
                    })
                    .toList();
            if (!unsatisfiedVariables.isEmpty()) {
                addViolation(AstUtils.getFullyQualifiedName(node),
                        getAllVariablesHavingUnexpectedTypeDescription(expectedType, unsatisfiedVariables));
            }
        } else {
            for (VariableDeclarator variableDeclarator : variables) {
                if (varToTypeMap.containsKey(variableDeclarator.getNameAsString())) {
                    String expectedType = varToTypeMap.get(variableDeclarator.getNameAsString());
                    Predicate<ResolvedType> predicateForExpectedType = getPredicateForType(expectedType);
                    ResolvedType resolvedType = codeCheckContext.getJavaParserFacade().convertToUsage(variableDeclarator.getType());
                    if (predicateForExpectedType.negate().test(resolvedType)) {
                        addViolation(AstUtils.getFullyQualifiedName(node), getVariableHavingUnexpectedTypeDescription(
                                variableDeclarator.getNameAsString(), expectedType, variableDeclarator.getTypeAsString()));
                    }
                }
            }
        }
    }

    @Override
    protected String getRuleName() {
        return "Проверка типов данных полей класса";
    }

    private String getWrongAmountOfVariablesDescription(int expectedAmount, int actualAmount) {
        return MessageFormat.format("Неверное количество полей в классе. Ожидалось: {0}. Найдено: {1}.",
                expectedAmount, actualAmount);
    }

    private String getAllVariablesHavingUnexpectedTypeDescription(String expectedType,
                                                                  List<VariableDeclarator> wrongVariables) {
        return MessageFormat.format("Неверный тип данных полей класса. Ожидалось: все поля имеют тип ''{0}''. По факту: " +
                        "поле(-я) {1} имеет(-ют) неверный тип.",
                TYPE_2_NAME_MAP.getOrDefault(expectedType, expectedType),
                wrongVariables.stream()
                        .map(vd -> vd.getName().asString())
                        .collect(Collectors.joining(", ", "'", "'"))
        );
    }

    private String getVariableHavingUnexpectedTypeDescription(String variableName, String expectedType, String actualType) {
        return MessageFormat.format("Неверный тип данных полей класса. Ожидалось: поле ''{0}'' имеет тип ''{1}''. " +
                        "Актуальный тип: ''{2}''.", variableName,
                TYPE_2_NAME_MAP.getOrDefault(expectedType, expectedType),
                actualType);
    }

    private Predicate<ResolvedType> getPredicateForType(String type) {
        return switch (type) {
            case NUMBER_TYPE -> Predicates.NUMBER_TYPE_PREDICATE;
            case INTEGER_TYPE -> Predicates.INT_NUMBER_TYPE_PREDICATE;
            case REAL_TYPE -> Predicates.REAL_NUMBER_TYPE_PREDICATE;
            case ANY_TYPE -> Predicates.TYPE_ANY_PREDICATE;
            case INT_OR_LONG_TYPE -> Predicates.INT_OR_LONG_TYPE_PREDICATE;
            case "java.lang.String" -> Predicates.STRING_TYPE_PREDICATE;
            default -> throw new IllegalArgumentException("Unknown type " + type);
        };
    }

    @Getter
    @AllArgsConstructor
    public static class ExpectedClassInputData {
        private int expectedAmountOfVariables;
        private Map<String, String> varToTypeMap;
    }
}
