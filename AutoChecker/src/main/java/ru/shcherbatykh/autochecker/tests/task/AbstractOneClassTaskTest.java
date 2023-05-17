package ru.shcherbatykh.autochecker.tests.task;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.shcherbatykh.autochecker.ast.AstUtils;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;
import ru.shcherbatykh.autochecker.model.CodeTestResult;
import ru.shcherbatykh.autochecker.model.CodeTestType;
import ru.shcherbatykh.autochecker.model.Status;
import ru.shcherbatykh.autochecker.rules.model.RuleContext;
import ru.shcherbatykh.autochecker.rules.model.RuleViolation;

import java.text.MessageFormat;
import java.util.*;

@Slf4j
public abstract class AbstractOneClassTaskTest extends AbstractTaskTest {
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
                        .type(CodeTestType.AST)
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

    protected abstract List<RuleViolation> checkRules(CodeCheckContext codeCheckContext,
                                                      ClassOrInterfaceDeclaration targetClass);

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
