package ru.shcherbatykh.codetester.tests;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.shcherbatykh.codetester.model.results.RunCodeResult;
import ru.shcherbatykh.codetester.services.ExpectedResultProvider;
import ru.shcherbatykh.codetester.ast.AstUtils;
import ru.shcherbatykh.codetester.model.CodeCheckContext;
import ru.shcherbatykh.common.model.CodeTestResult;
import ru.shcherbatykh.common.model.CodeTestType;
import ru.shcherbatykh.common.model.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("runCode")
public class RunProgramTest implements CodeTest {
    private static final Charset CP1251_CHARSET = Charset.forName("cp1251");

    private static final String TIMEOUT_ERROR = "Превышено максимальное время ожидания";
    private static final String FAILED_ERROR = "Работа программы завершилось неудачей";
    private static final String NO_MAIN_CLASS_ERROR = "Класс с методом main не найден";

    @Value("${autochecker.code_tester.run_code_test.process_timeout_seconds}")
    private int processTimeout;

    private final ExpectedResultProvider expectedResultProvider;

    @Autowired
    public RunProgramTest(ExpectedResultProvider expectedResultProvider) {
        this.expectedResultProvider = expectedResultProvider;
    }

    @Override
    public List<CodeTestResult> launchTest(CodeCheckContext codeCheckContext) {
        if (codeCheckContext.getMainUnit() == null) {
            RunCodeResult result = RunCodeResult.builder()
                    .error(NO_MAIN_CLASS_ERROR)
                    .build();
            return Collections.singletonList(CodeTestResult.builder()
                    .status(Status.NOK)
                    .type(CodeTestType.RUN)
                    .result(createRunCodeResultNode(result))
                    .build());
        }

        String mainClassPath = AstUtils.getFullyQualifiedName(codeCheckContext.getMainClass());
        return Collections.singletonList(runJavaProcess(codeCheckContext, mainClassPath));
    }

    @SneakyThrows
    private CodeTestResult runJavaProcess(CodeCheckContext codeCheckContext, String mainClassPath) {
        String[] commands = new String[]{"java", mainClassPath};
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(codeCheckContext.getRequestPath().toFile());
        Process process = processBuilder.start();
        boolean isCompleted = process.waitFor(processTimeout, TimeUnit.SECONDS);
        if (!isCompleted) {
            terminateProcess(process);
            RunCodeResult result = RunCodeResult.builder()
                    .error(TIMEOUT_ERROR)
                    .build();
            return CodeTestResult.builder()
                    .status(Status.NOK)
                    .type(CodeTestType.RUN)
                    .result(createRunCodeResultNode(result))
                    .build();
        }
        if (process.exitValue() != 0) {
            String errorResponse = collectResponseFromStream(process.getErrorStream());
            RunCodeResult result = RunCodeResult.builder()
                    .actualValue(errorResponse)
                    .error(FAILED_ERROR)
                    .build();
            return CodeTestResult.builder()
                    .status(Status.NOK)
                    .type(CodeTestType.RUN)
                    .result(createRunCodeResultNode(result))
                    .build();
        }

        String expectedValue = expectedResultProvider.getExpectedResultForTaskLaunch(codeCheckContext.getTaskId());
        String actualValue = collectResponseFromStream(process.getInputStream());
        RunCodeResult result = RunCodeResult.builder()
                .expectedValue(expectedValue)
                .actualValue(actualValue)
                .build();
        return CodeTestResult.builder()
                .status(Objects.equals(expectedValue, actualValue) ? Status.OK : Status.NOK)
                .type(CodeTestType.RUN)
                .result(createRunCodeResultNode(result))
                .build();
    }

    private void terminateProcess(Process process) {
        try {
            process.destroy();
        } catch (Throwable t) {
            log.error("Error during process termination. Process Info: " + process.info(), t);
        }
    }

    private String collectResponseFromStream(InputStream stream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(stream, CP1251_CHARSET);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            stringBuilder.append(line);
            if ((line = reader.readLine()) != null) {
                stringBuilder.append("\n");
            }
        }
        return encodeToBase64(stringBuilder.toString());
    }

    private String encodeToBase64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
