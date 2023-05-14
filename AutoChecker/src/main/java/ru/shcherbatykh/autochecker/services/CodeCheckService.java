package ru.shcherbatykh.autochecker.services;

import com.github.javaparser.ParseException;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.ast.CodeSourceASTParser;
import ru.shcherbatykh.autochecker.broker.KafkaMessageProducer;
import ru.shcherbatykh.autochecker.model.*;
import ru.shcherbatykh.autochecker.tests.CodeTest;
import ru.shcherbatykh.autochecker.tests.CompilationTest;
import ru.ssu.csit.cs.autochecker.model.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CodeCheckService {
    private final CodeSourceASTParser codeSourceASTParser;
    private final FileWriterService fileWriterService;
    private final TaskCodeTestsProvider taskCodeTestsProvider;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final CompilationTest compilationTest;

    @Autowired
    public CodeCheckService(CodeSourceASTParser codeSourceASTParser,
                            FileWriterService fileWriterService,
                            TaskCodeTestsProvider taskCodeTestsProvider,
                            KafkaMessageProducer kafkaMessageProducer,
                            CompilationTest compilationTest) {
        this.codeSourceASTParser = codeSourceASTParser;
        this.fileWriterService = fileWriterService;
        this.taskCodeTestsProvider = taskCodeTestsProvider;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.compilationTest = compilationTest;
    }

    public void checkCode(CodeCheckRequest codeCheckRequest) {
        CodeCheckResponse result;
        try {
            result = checkCodeInternal(codeCheckRequest);
        } catch (Throwable t) {
            log.error("Error during checking code sources", t);
            result = CodeCheckResponse.builder()
                    .code(ResponseCode.CH_002.getCode())
                    .message(ResponseCode.CH_002.getMessage())
                    .build();
        }
        kafkaMessageProducer.sendCodeCheckResultMessage(result);
    }

    public CodeCheckResponse checkCodeImmediately(CodeCheckRequest codeCheckRequest) {
        CodeCheckResponse result;
        try {
            result = checkCodeInternal(codeCheckRequest);
        } catch (Throwable t) {
            log.error("Error during checking code sources", t);
            result = CodeCheckResponse.builder()
                    .code(ResponseCode.CH_002.getCode())
                    .message(ResponseCode.CH_002.getMessage())
                    .build();
        }
        return result;
    }

    private CodeCheckResponse checkCodeInternal(CodeCheckRequest codeCheckRequest) {
        CodeCheckContext codeCheckContext;
        try {
            codeCheckContext = codeSourceASTParser.parseCodeSources(codeCheckRequest);
            fileWriterService.saveCompilationSources(codeCheckContext);
        } catch (ParseException | ParseProblemException e) {
            return CodeCheckResponse.builder()
                    .code(ResponseCode.CH_003.getCode())
                    .message(ResponseCode.CH_003.getMessage() + ": " + e.getMessage())
                    .build();
        }

        JavaParserFacade javaParserFacade = createJavaParserFacade(codeCheckContext.getRequestPath());
        codeCheckContext.setJavaParserFacade(javaParserFacade);

        CodeTestResult compilationResult = compilationTest.launchTest(codeCheckContext);
        if (compilationResult.getStatus() == Status.NOK) {
            return CodeCheckResponse.builder()
                    .code(ResponseCode.CH_004.getCode())
                    .message(ResponseCode.CH_004.getMessage())
                    .results(Collections.singletonList(compilationResult))
                    .build();
        }

        List<CodeTest> codeTests = taskCodeTestsProvider.getTestChain(codeCheckContext.getTaskId());
        log.debug("testChain for task {} is: {}", codeCheckContext.getTaskId(), codeTests);

        List<CodeTestResult> results = new ArrayList<>(codeTests.size() + 1);
        results.add(compilationResult);

        boolean allTestPassed = true;
        for (CodeTest codeTest : codeTests) {
            CodeTestResult codeTestResult = codeTest.launchTest(codeCheckContext);
            if (codeTestResult.getStatus() != Status.OK) {
                allTestPassed = false;
            }
            results.add(codeTestResult);
        }

        ResponseCode responseCode = allTestPassed ? ResponseCode.CH_000 : ResponseCode.CH_001;
        return CodeCheckResponse.builder()
                .code(responseCode.getCode())
                .message(responseCode.getMessage())
                .results(results)
                .build();
    }

    private static JavaParserFacade createJavaParserFacade(Path requestPath) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(requestPath));
        return JavaParserFacade.get(combinedTypeSolver);
    }
}
