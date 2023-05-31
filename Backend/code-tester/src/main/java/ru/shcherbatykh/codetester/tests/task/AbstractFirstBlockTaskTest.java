package ru.shcherbatykh.codetester.tests.task;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.shcherbatykh.codetester.rules.ToStringMethodExistsRule;
import ru.shcherbatykh.codetester.rules.model.RuleContext;
import ru.shcherbatykh.codetester.rules.model.RuleViolation;
import ru.shcherbatykh.codetester.spring.BeanHelper;
import ru.shcherbatykh.codetester.ast.AstUtils;
import ru.shcherbatykh.codetester.model.CodeCheckContext;
import ru.shcherbatykh.codetester.rules.ClassConstructorRule;
import ru.shcherbatykh.codetester.rules.ClassVariableTypesRule;
import ru.shcherbatykh.common.constants.Constants;
import ru.shcherbatykh.common.model.CodeTestResult;
import ru.shcherbatykh.common.model.CodeTestType;
import ru.shcherbatykh.common.model.Status;

import java.text.MessageFormat;
import java.util.*;

@Slf4j
public abstract class AbstractFirstBlockTaskTest extends AbstractTaskTest {
    private final ClassConstructorRule classConstructorRule;
    private final ClassVariableTypesRule classVariableTypesRule;
    private final ToStringMethodExistsRule toStringMethodExistsRule;

    public AbstractFirstBlockTaskTest() {
        this.classConstructorRule = BeanHelper.getBean(ClassConstructorRule.class);
        this.classVariableTypesRule = BeanHelper.getBean(ClassVariableTypesRule.class);
        this.toStringMethodExistsRule = BeanHelper.getBean(ToStringMethodExistsRule.class);
    }

    @Override
    public List<CodeTestResult> launchTest(CodeCheckContext codeCheckContext) {
        Pair<Boolean, CodeTestResult> precheck = validateTotalCountOfTypes(codeCheckContext, getExpectedAmountOfTypes());
        if (precheck.getLeft()) {
            return Collections.singletonList(precheck.getRight());
        }

        Map<ClassOrInterfaceDeclaration, CompilationUnit> nonMainClasses = findNonMainClasses(codeCheckContext);
        log.debug("nonMainClasses: {}", nonMainClasses);
        precheck = validateAmountOfNonMainClasses(getExpectedAmountOfNonMainClasses(), nonMainClasses.size());
        if (precheck.getLeft()) {
            return Collections.singletonList(precheck.getRight());
        }
        codeCheckContext.setNonMainClasses(nonMainClasses);

        ClassOrInterfaceDeclaration targetClass = nonMainClasses.keySet().iterator().next();
        log.debug("targetClass: {}", targetClass);

        List<RuleViolation> violations = checkRules(codeCheckContext, targetClass);
        if (violations.isEmpty()) {
            return List.of(CodeTestResult.OK_AST_RESULT, CodeTestResult.OK_REFLEXIVITY_RESULT);
        } else {
            List<CodeTestResult> results = new ArrayList<>(2);
            List<RuleViolation> astViolations = violations.stream()
                    .filter(ruleViolation -> !ruleViolation.reflexivity())
                    .toList();
            if (!astViolations.isEmpty()) {
                results.add(CodeTestResult.builder()
                        .status(Status.NOK)
                        .type(CodeTestType.AST)
                        .result(createRuleViolationsNode(astViolations))
                        .build());
            }
            List<RuleViolation> reflexivityViolations = violations.stream()
                    .filter(RuleViolation::reflexivity)
                    .toList();
            if (!reflexivityViolations.isEmpty()) {
                results.add(CodeTestResult.builder()
                        .status(Status.NOK)
                        .type(CodeTestType.REFLEXIVITY)
                        .result(createRuleViolationsNode(violations))
                        .build());
            }
            return results;
        }
    }

    protected void checkMainClass(CodeCheckContext codeCheckContext,
                                  ClassOrInterfaceDeclaration targetClass,
                                  RuleContext ruleContext) {
        MethodDeclaration mainMethod = codeCheckContext.getMainMethod();
        Optional<BlockStmt> body = mainMethod.getBody();
        if (body.isPresent()) {
            List<ObjectCreationExpr> pointCreationExpressions = body.get().findAll(ObjectCreationExpr.class,
                    objectCreationExpr -> AstUtils.isExpectedReferenceType(objectCreationExpr.getType(),
                            codeCheckContext.getJavaParserFacade(), targetClass));
            if (pointCreationExpressions.size() != getExpectedAmountOfCreatedClasses()) {
                addViolationOnInvalidAmountOfCreatedEntities(codeCheckContext, targetClass, ruleContext, pointCreationExpressions.size());
            }
        } else {
            addViolationOnInvalidAmountOfCreatedEntities(codeCheckContext, targetClass, ruleContext, 0);
        }
    }

    protected abstract int getExpectedAmountOfTypes();

    protected abstract int getExpectedAmountOfNonMainClasses();

    protected abstract int getExpectedAmountOfCreatedClasses();

    protected abstract Map<String, Object> getClassConstructorRuleInputContext();

    protected abstract Map<String, Object> getClassVariableTypesRuleInputContext();

    protected abstract void checkReflectively(ClassOrInterfaceDeclaration targetClass,
                                              Class<?> nonMainClassByDeclaration,
                                              RuleContext ruleContext);

    protected List<RuleViolation> checkRules(CodeCheckContext codeCheckContext,
                                             ClassOrInterfaceDeclaration targetClass) {
        RuleContext ruleContext = new RuleContext();

        classConstructorRule.setContexts(ruleContext, getClassConstructorRuleInputContext());
        classConstructorRule.applyRule(targetClass, codeCheckContext);

        classVariableTypesRule.setContexts(ruleContext, getClassVariableTypesRuleInputContext());
        classVariableTypesRule.applyRule(targetClass, codeCheckContext);

        toStringMethodExistsRule.setContext(ruleContext);
        toStringMethodExistsRule.applyRule(targetClass, codeCheckContext);

        checkMainClass(codeCheckContext, targetClass, ruleContext);

        if (!ruleContext.getData().containsKey(Constants.SKIP_REFLEXIVITY_CREATION)
                || !ruleContext.getData().get(Constants.SKIP_REFLEXIVITY_CREATION).containsKey(targetClass)
                || !((boolean) ruleContext.getData().get(Constants.SKIP_REFLEXIVITY_CREATION).get(targetClass))) {
            checkReflectively(targetClass, codeCheckContext.findNonMainClassByDeclaration(targetClass), ruleContext);
        }

        return ruleContext.getViolations();
    }

    protected void addViolationForToStringMethod(ClassOrInterfaceDeclaration targetClass,
                                                 RuleContext ruleContext,
                                                 String expectedResult,
                                                 String actualResult) {
        RuleViolation violation = new RuleViolation("Проверка результата метода toString",
                AstUtils.getFullyQualifiedName(targetClass),
                MessageFormat.format("Неверный результат работы метода toString. Ожидалось: {0}. Найдено: {1}.",
                        expectedResult, actualResult),
                true
        );
        ruleContext.addViolation(violation);
    }

    private void addViolationOnInvalidAmountOfCreatedEntities(CodeCheckContext codeCheckContext,
                                                              ClassOrInterfaceDeclaration pointClass,
                                                              RuleContext ruleContext,
                                                              int actualSize) {
        RuleViolation violation = new RuleViolation("Проверка числа созданных сущностей",
                AstUtils.getFullyQualifiedName(codeCheckContext.getMainClass()),
                MessageFormat.format("Неверное число выражений создания объектов типа ''{0}''. Ожидалось: {1}. Найдено: {2}.",
                        AstUtils.getFullyQualifiedName(pointClass), getExpectedAmountOfCreatedClasses(),
                        actualSize)
        );
        ruleContext.addViolation(violation);
    }
}
