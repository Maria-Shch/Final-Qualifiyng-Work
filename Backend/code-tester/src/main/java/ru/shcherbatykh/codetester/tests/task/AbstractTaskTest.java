package ru.shcherbatykh.codetester.tests.task;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.lang3.tuple.Pair;
import ru.shcherbatykh.codetester.tests.CodeTest;
import ru.shcherbatykh.codetester.model.CodeCheckContext;
import ru.shcherbatykh.common.model.CodeTestResult;
import ru.shcherbatykh.common.model.CodeTestType;
import ru.shcherbatykh.common.model.Status;

import java.text.MessageFormat;
import java.util.*;

public abstract class AbstractTaskTest implements CodeTest {
    protected Pair<Boolean, CodeTestResult> validateTotalCountOfTypes(CodeCheckContext codeCheckContext,
                                                                      int expectedAmountOfTypes) {
        long actualAmountOfTypes = codeCheckContext.getCompilationUnits().stream()
                .map(compilationUnit -> compilationUnit.findAll(TypeDeclaration.class))
                .mapToLong(Collection::size)
                .sum();
        if (actualAmountOfTypes != expectedAmountOfTypes) {
            CodeTestResult codeTestResult = buildFailedResult(
                    MessageFormat.format("Неверное число классов. Ожидалось: {0}. Найдено: {1}.",
                            expectedAmountOfTypes, actualAmountOfTypes));
            return Pair.of(true, codeTestResult);
        }
        return Pair.of(false, null);
    }

    protected Pair<Boolean, CodeTestResult> validateAmountOfNonMainClasses(long expectedAmountOfNonMainClasses,
                                                                           long actualAmountOfNonMainClasses) {
        if (expectedAmountOfNonMainClasses != actualAmountOfNonMainClasses) {
            return Pair.of(true, buildFailedResult(
                    MessageFormat.format("Неверное число неосновных классов. Ожидалось: {0}. Найдено: {1}.",
                            expectedAmountOfNonMainClasses, actualAmountOfNonMainClasses)));
        }
        return Pair.of(false, null);
    }

    protected Map<ClassOrInterfaceDeclaration, CompilationUnit> findNonMainClasses(CodeCheckContext codeCheckContext) {
        Map<ClassOrInterfaceDeclaration, CompilationUnit> nonMainClasses = new HashMap<>();
        codeCheckContext.getCompilationUnits().stream()
                .filter(compilationUnit -> !Objects.equals(compilationUnit, codeCheckContext.getMainUnit()))
                .forEach(compilationUnit -> {
                    List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class,
                            ClassOrInterfaceDeclaration::isClassOrInterfaceDeclaration);
                    classes.forEach(clazz -> nonMainClasses.put(clazz, compilationUnit));
                });
        return nonMainClasses;
    }

    private CodeTestResult buildFailedResult(String details) {
        return CodeTestResult.builder()
                .status(Status.NOK)
                .type(CodeTestType.AST)
                .result(createDescriptionNode(details))
                .build();
    }
}
