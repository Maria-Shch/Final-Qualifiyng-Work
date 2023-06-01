package ru.shcherbatykh.codetester.services;

import com.github.javaparser.ParseException;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.codetester.ast.CodeSourceASTParser;
import ru.shcherbatykh.codetester.broker.KafkaMessageProducer;
import ru.shcherbatykh.codetester.class_loader.PathClassLoader;
import ru.shcherbatykh.codetester.model.CodeCheckContext;
import ru.shcherbatykh.codetester.tests.CodeTest;
import ru.shcherbatykh.codetester.tests.CompilationTest;
import ru.shcherbatykh.common.model.*;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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
        kafkaMessageProducer.sendCodeCheckResultMessage(prepareResult(codeCheckRequest));
    }

    private CodeCheckResponse prepareResult(CodeCheckRequest codeCheckRequest) {
        try {
            return checkCodeInternal(codeCheckRequest);
        } catch (Throwable t) {
            log.error("Error during checking code sources", t);
            return CodeCheckResponse.builder()
                    .studentId(codeCheckRequest.getStudentId())
                    .taskId(codeCheckRequest.getTaskId())
                    .requestUuid(codeCheckRequest.getRequestUuid())
                    .code(ResponseCode.CH_002.getCode())
                    .message(ResponseCode.CH_002.getMessage())
                    .build();
        }
    }

    private CodeCheckResponse checkCodeInternal(CodeCheckRequest codeCheckRequest) {
        CodeCheckContext codeCheckContext;
        try {
            codeCheckContext = codeSourceASTParser.parseCodeSources(codeCheckRequest);
            fileWriterService.saveStudentCompilationSources(codeCheckContext);
        } catch (ParseProblemException e) {
            String message = e.getProblems().stream()
                    .map(Problem::getMessage)
                    .collect(Collectors.joining("\n"));
            return CodeCheckResponse.builder()
                    .studentId(codeCheckRequest.getStudentId())
                    .taskId(codeCheckRequest.getTaskId())
                    .requestUuid(codeCheckRequest.getRequestUuid())
                    .code(ResponseCode.CH_003.getCode())
                    .message(message)
                    .build();
        } catch (ParseException e) {
            return CodeCheckResponse.builder()
                    .studentId(codeCheckRequest.getStudentId())
                    .taskId(codeCheckRequest.getTaskId())
                    .requestUuid(codeCheckRequest.getRequestUuid())
                    .code(ResponseCode.CH_003.getCode())
                    .message(e.getMessage())
                    .build();
        }

        JavaParserFacade javaParserFacade = createJavaParserFacade(codeCheckContext.getRequestPath());
        codeCheckContext.setJavaParserFacade(javaParserFacade);

        CodeTestResult compilationResult = compilationTest.launchTest(codeCheckContext).get(0);
        if (compilationResult.getStatus() == Status.NOK) {
            return CodeCheckResponse.builder()
                    .studentId(codeCheckRequest.getStudentId())
                    .taskId(codeCheckRequest.getTaskId())
                    .requestUuid(codeCheckRequest.getRequestUuid())
                    .code(ResponseCode.CH_004.getCode())
                    .message(ResponseCode.CH_004.getMessage())
                    .results(Collections.singletonList(compilationResult))
                    .build();
        }

        PathClassLoader pathClassLoader = new PathClassLoader(new HashSet<>(codeCheckContext.getJavaFilePaths().values()));
        Map<Path, Class<?>> loadedClasses = pathClassLoader.findAllClasses();
        codeCheckContext.setLoadedClasses(loadedClasses);

        List<CodeTest> codeTests = taskCodeTestsProvider.getTestChain(codeCheckContext.getTaskId());
        log.debug("testChain for task {} is: {}", codeCheckContext.getTaskId(), codeTests);

        List<CodeTestResult> results = new ArrayList<>(codeTests.size() + 1);
        results.add(compilationResult);

        boolean allTestPassed = true;
        for (CodeTest codeTest : codeTests) {
            List<CodeTestResult> codeTestResults = codeTest.launchTest(codeCheckContext);
            if (codeTestResults.stream().anyMatch(ctr -> ctr.getStatus() != Status.OK)) {
                allTestPassed = false;
            }
            results.addAll(codeTestResults);
        }

        ResponseCode responseCode = allTestPassed ? ResponseCode.CH_000 : ResponseCode.CH_001;
        return CodeCheckResponse.builder()
                .studentId(codeCheckRequest.getStudentId())
                .taskId(codeCheckRequest.getTaskId())
                .requestUuid(codeCheckRequest.getRequestUuid())
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
