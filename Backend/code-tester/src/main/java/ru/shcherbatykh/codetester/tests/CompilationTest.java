package ru.shcherbatykh.codetester.tests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.codetester.model.CodeCheckContext;
import ru.shcherbatykh.codetester.model.results.CompileCodeResult;
import ru.shcherbatykh.codetester.services.CompilationService;
import ru.shcherbatykh.common.model.CodeTestResult;
import ru.shcherbatykh.common.model.CodeTestType;
import ru.shcherbatykh.common.model.Status;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("compileCode")
public class CompilationTest implements CodeTest {
    private final CompilationService compilationService;

    @Autowired
    public CompilationTest(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @Override
    public List<CodeTestResult> launchTest(CodeCheckContext codeCheckContext) {
        Set<Path> compilationUnits = new HashSet<>(codeCheckContext.getJavaFilePaths().values());
        CompileCodeResult compileCodeResult = compilationService.compileJavaClasses(compilationUnits);
        if (compileCodeResult.getStatus() == Status.NOK) {
            codeCheckContext.setCompilationPassed(false);
            return Collections.singletonList(CodeTestResult.builder()
                    .status(Status.NOK)
                    .type(CodeTestType.COMPILE)
                    .result(createCompilationErrorsNode(compileCodeResult.getErrors()))
                    .build());
        }

        codeCheckContext.setCompilationPassed(true);
        return Collections.singletonList(CodeTestResult.OK_COMPILATION_RESULT);
    }
}
