package ru.shcherbatykh.autochecker.tests.task;

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
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractOneClassTaskTest extends AbstractTaskTest {
    @Override
    public CodeTestResult launchTest(CodeCheckContext codeCheckContext) {
        Pair<Boolean, CodeTestResult> precheck = validateTotalCountOfTypes(codeCheckContext, getExpectedAmountOfTypes());
        if (precheck.getLeft()) {
            return precheck.getRight();
        }

        List<ClassOrInterfaceDeclaration> nonMainClasses = findNonMainClasses(codeCheckContext);
        log.debug("nonMainClasses: {}", nonMainClasses);
        precheck = validateAmountOfNonMainClasses(getExpectedAmountOfNonMainClasses(), nonMainClasses.size());
        if (precheck.getLeft()) {
            return precheck.getRight();
        }

        ClassOrInterfaceDeclaration targetClass = nonMainClasses.iterator().next();
        log.debug("targetClass: {}", targetClass);

        List<RuleViolation> violations = checkRules(codeCheckContext, targetClass);
        if (violations.isEmpty()) {
            return CodeTestResult.OK_AST_RESULT;
        } else {
            return CodeTestResult.builder()
                    .status(Status.NOK)
                    .type(CodeTestType.AST)
                    .result(createRunViolationsNode(violations))
                    .build();
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
