package ru.shcherbatykh.autochecker.services;

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
import ru.shcherbatykh.Backend.classes.CodeCheckRequest;
import ru.shcherbatykh.autochecker.ast.CodeSourceASTParser;
import ru.shcherbatykh.autochecker.broker.KafkaMessageProducer;
import ru.shcherbatykh.autochecker.class_loader.TaskClassLoader;
import ru.shcherbatykh.autochecker.model.*;
import ru.shcherbatykh.autochecker.tests.CheckCodeStyleTest;
import ru.shcherbatykh.autochecker.tests.CodeTest;
import ru.shcherbatykh.autochecker.tests.CompilationTest;

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
        CodeCheckResponse result;
        try {
            result = checkCodeInternal(codeCheckRequest);
        } catch (Throwable t) {
            log.error("Error during checking code sources", t);
            result = CodeCheckResponse.builder()
                    .studentId(codeCheckRequest.getStudentId())
                    .taskId(codeCheckRequest.getTaskId())
                    .taskPath(codeCheckRequest.getTaskPath())
                    .requestUuid(codeCheckRequest.getRequestUuid())
                    .code(ResponseCode.CH_002.getCode())
                    .message(ResponseCode.CH_002.getMessage())
                    .build();
        }
        kafkaMessageProducer.sendCodeCheckResultMessage(result);
    }

    private CodeCheckResponse checkCodeInternal(CodeCheckRequest codeCheckRequest) {
        CodeCheckContext codeCheckContext;
        try {
            codeCheckContext = codeSourceASTParser.parseCodeSources(codeCheckRequest);
            fileWriterService.saveCompilationSources(codeCheckContext);
        } catch (ParseException | ParseProblemException e) {
            String message;
            if (e instanceof ParseProblemException ppe) {
                 message = ppe.getProblems().stream()
                         .map(Problem::getMessage)
                         .collect(Collectors.joining("\n"));
            } else {
                message = e.getMessage();
            }
            return CodeCheckResponse.builder()
                    .studentId(codeCheckRequest.getStudentId())
                    .taskPath(codeCheckRequest.getTaskPath())
                    .taskId(codeCheckRequest.getTaskId())
                    .requestUuid(codeCheckRequest.getRequestUuid())
                    .code(ResponseCode.CH_003.getCode())
                    .message(message)
                    .build();
        }

        JavaParserFacade javaParserFacade = createJavaParserFacade(codeCheckContext.getRequestPath());
        codeCheckContext.setJavaParserFacade(javaParserFacade);

        CodeTestResult compilationResult = compilationTest.launchTest(codeCheckContext).get(0);
        if (compilationResult.getStatus() == Status.NOK) {
            return CodeCheckResponse.builder()
                    .studentId(codeCheckRequest.getStudentId())
                    .taskId(codeCheckRequest.getTaskId())
                    .taskPath(codeCheckRequest.getTaskPath())
                    .requestUuid(codeCheckRequest.getRequestUuid())
                    .code(ResponseCode.CH_004.getCode())
                    .message(ResponseCode.CH_004.getMessage())
                    .results(Collections.singletonList(compilationResult))
                    .build();
        }

        TaskClassLoader taskClassLoader = new TaskClassLoader(new HashSet<>(codeCheckContext.getJavaFilePaths().values()));
        Map<Path, Class<?>> loadedClasses = taskClassLoader.findAllClasses();
        codeCheckContext.setLoadedClasses(loadedClasses);

        List<CodeTest> codeTests = taskCodeTestsProvider.getTestChain(codeCheckContext.getTaskPath());
        log.debug("testChain for task {} is: {}", codeCheckContext.getTaskPath(), codeTests);

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
                .taskPath(codeCheckRequest.getTaskPath())
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
