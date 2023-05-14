package ru.shcherbatykh.autochecker.tests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;
import ru.shcherbatykh.autochecker.model.CodeTestResult;
import ru.shcherbatykh.autochecker.model.CodeTestType;
import ru.shcherbatykh.autochecker.model.Status;
import ru.shcherbatykh.autochecker.model.results.CompileCodeResult;
import ru.shcherbatykh.autochecker.services.CompilationService;

@Slf4j
@Component("compileCode")
public class CompilationTest implements CodeTest {
    private final CompilationService compilationService;

    @Autowired
    public CompilationTest(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @Override
    public CodeTestResult launchTest(CodeCheckContext codeCheckContext) {
        CompileCodeResult compileCodeResult = compilationService.compileJavaClasses(codeCheckContext.getJavaFilePaths());
        if (compileCodeResult.getStatus() == Status.NOK) {
            codeCheckContext.setCompilationPassed(false);
            return CodeTestResult.builder()
                    .status(Status.NOK)
                    .type(CodeTestType.COMPILE)
                    .result(createCompilationErrorsNode(compileCodeResult.getErrors()))
                    .build();
        }

        codeCheckContext.setCompilationPassed(true);
        return CodeTestResult.OK_COMPILATION_RESULT;
    }
}
