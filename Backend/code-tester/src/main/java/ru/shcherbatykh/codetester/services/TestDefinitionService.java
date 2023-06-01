package ru.shcherbatykh.codetester.services;

import com.github.javaparser.ParseException;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.codetester.ast.AstUtils;
import ru.shcherbatykh.codetester.ast.CodeSourceASTParser;
import ru.shcherbatykh.codetester.broker.KafkaMessageProducer;
import ru.shcherbatykh.codetester.class_loader.PathClassLoader;
import ru.shcherbatykh.codetester.model.TestDefinitionContext;
import ru.shcherbatykh.codetester.model.results.CompileCodeResult;
import ru.shcherbatykh.codetester.tests.CodeTest;
import ru.shcherbatykh.codetester.tests.task.TaskTest;
import ru.shcherbatykh.common.model.*;

import javax.annotation.PostConstruct;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TestDefinitionService {
    private static final char PKG_SEPARATOR = '.';
    private static final char DIR_SEPARATOR = '/';
    private static final String BACKUP_EXTENSION = ".bak.";
    private static final String TEST_PACKAGE = "ru.shcherbatykh.tests.task";

    @Value("${autochecker.hot_reload.io.test_classes_folder}")
    private String testClassesFolder;

    @Value("${autochecker.hot_reload.backup_date_format}")
    private String backupDateFormat;

    @Value("${autochecker.hot_reload.io.backup_test_classes_folder}")
    private String backupTestClassesFolder;

    private final CodeSourceASTParser codeSourceASTParser;
    private final FileWriterService fileWriterService;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final CompilationService compilationService;
    private final TestClassProvider testClassProvider;

    private DateTimeFormatter dateTimeFormatter;

    @Autowired
    public TestDefinitionService(CodeSourceASTParser codeSourceASTParser,
                                 FileWriterService fileWriterService,
                                 KafkaMessageProducer kafkaMessageProducer,
                                 CompilationService compilationService,
                                 TestClassProvider testClassProvider) {
        this.codeSourceASTParser = codeSourceASTParser;
        this.fileWriterService = fileWriterService;
        this.kafkaMessageProducer = kafkaMessageProducer;
        this.compilationService = compilationService;
        this.testClassProvider = testClassProvider;
    }

    @PostConstruct
    public void init() {
        dateTimeFormatter = DateTimeFormatter.ofPattern(backupDateFormat);
    }

    public void defineNewTest(TestDefinitionRequest testDefinitionRequest) {
        kafkaMessageProducer.sendTestDefinitionResultMessage(prepareResult(testDefinitionRequest));
    }

    private TestDefinitionResponse prepareResult(TestDefinitionRequest testDefinitionRequest) {
        try {
            return defineNewTestInternal(testDefinitionRequest);
        } catch (Throwable t) {
            log.error("Error during handling test code", t);
            return TestDefinitionResponse.builder()
                    .taskId(testDefinitionRequest.getTaskId())
                    .requestUuid(testDefinitionRequest.getRequestUuid())
                    .code(TestDefinitionResponseCode.TD_001.getCode())
                    .message(TestDefinitionResponseCode.TD_001.getMessage())
                    .technicalErrorInfo(new TestDefinitionTechnicalErrorInfo(ExceptionUtils.getStackTrace(t)))
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private TestDefinitionResponse defineNewTestInternal(TestDefinitionRequest testDefinitionRequest) {
        TestDefinitionContext testDefinitionContext;
        try {
            testDefinitionContext = codeSourceASTParser.parseTestCodeSource(testDefinitionRequest);
            enrichCompilationUnit(testDefinitionContext);
            fileWriterService.saveCompilationUnit(testDefinitionContext);
        } catch (ParseProblemException e) {
            List<String> problems = e.getProblems().stream()
                    .map(Problem::getMessage)
                    .toList();
            return TestDefinitionResponse.builder()
                    .taskId(testDefinitionRequest.getTaskId())
                    .requestUuid(testDefinitionRequest.getRequestUuid())
                    .code(TestDefinitionResponseCode.TD_002.getCode())
                    .message(TestDefinitionResponseCode.TD_002.getMessage())
                    .validationInfo(new TestDefinitionValidationInfo(problems))
                    .build();
        } catch (ParseException e) {
            return TestDefinitionResponse.builder()
                    .taskId(testDefinitionRequest.getTaskId())
                    .requestUuid(testDefinitionRequest.getRequestUuid())
                    .code(TestDefinitionResponseCode.TD_002.getCode())
                    .message(TestDefinitionResponseCode.TD_002.getMessage())
                    .validationInfo(new TestDefinitionValidationInfo(Collections.singletonList(e.getMessage())))
                    .build();
        }

        CompileCodeResult compileCodeResult = compilationService.compileJavaClasses(
                Collections.singletonList(testDefinitionContext.getJavaFilePath())
        );
        TestDefinitionCompilationInfo compilationInfo = createCompilationInfo(compileCodeResult);
        if (compileCodeResult.getStatus() == Status.NOK) {
            return TestDefinitionResponse.builder()
                    .taskId(testDefinitionRequest.getTaskId())
                    .requestUuid(testDefinitionRequest.getRequestUuid())
                    .code(TestDefinitionResponseCode.TD_003.getCode())
                    .message(TestDefinitionResponseCode.TD_003.getMessage())
                    .compilationInfo(compilationInfo)
                    .build();
        }

        backupPreviousTestVersion(testDefinitionContext);
        copyFilesToTestFolderPath(testDefinitionContext);

        List<Path> paths = Collections.singletonList(testDefinitionContext.getTestFolderClassFilePath());
        PathClassLoader pathClassLoader = new PathClassLoader(paths);
        Class<?> testClass = pathClassLoader.findAllClasses().values().iterator().next();
        if (!CodeTest.class.isAssignableFrom(testClass)) {
            return TestDefinitionResponse.builder()
                    .taskId(testDefinitionRequest.getTaskId())
                    .requestUuid(testDefinitionRequest.getRequestUuid())
                    .code(TestDefinitionResponseCode.TD_002.getCode())
                    .message(TestDefinitionResponseCode.TD_002.getMessage())
                    .validationInfo(new TestDefinitionValidationInfo(Collections.singletonList("Класс теста должен " +
                            "реализовывать интерфейс " + CodeTest.class.getName() + ".")))
                    .build();
        }

        try {
            Constructor<?> declaredConstructor = testClass.getDeclaredConstructor();
            if (!Modifier.isPublic(declaredConstructor.getModifiers())) {
                throw new NoSuchMethodException();
            }
        } catch (NoSuchMethodException e) {
            return TestDefinitionResponse.builder()
                    .taskId(testDefinitionRequest.getTaskId())
                    .requestUuid(testDefinitionRequest.getRequestUuid())
                    .code(TestDefinitionResponseCode.TD_002.getCode())
                    .message(TestDefinitionResponseCode.TD_002.getMessage())
                    .validationInfo(new TestDefinitionValidationInfo(Collections.singletonList("Класс теста должен " +
                            "иметь публичный конструктор без параметров.")))
                    .build();
        }

        testClassProvider.defineTestClass(testDefinitionContext.getTaskId(), (Class<CodeTest>) testClass,
                testDefinitionContext.getTestFolderJavaFilePath());

        return TestDefinitionResponse.builder()
                .taskId(testDefinitionRequest.getTaskId())
                .requestUuid(testDefinitionRequest.getRequestUuid())
                .code(TestDefinitionResponseCode.TD_000.getCode())
                .message(TestDefinitionResponseCode.TD_000.getMessage())
                .compilationInfo(compilationInfo)
                .build();
    }

    private void enrichCompilationUnit(TestDefinitionContext testDefinitionContext) throws ParseException {
        String taskId = testDefinitionContext.getTaskId();
        CompilationUnit compilationUnit = testDefinitionContext.getCompilationUnit();

        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        if (packageDeclaration.isPresent()) {
            packageDeclaration.get().setName(TEST_PACKAGE);
        } else {
            compilationUnit.setPackageDeclaration(TEST_PACKAGE);
        }

        ClassOrInterfaceDeclaration classDeclaration = compilationUnit.getTypes().stream()
                .filter(TypeDeclaration::isTopLevelType)
                .filter(TypeDeclaration::isPublic)
                .filter(TypeDeclaration::isClassOrInterfaceDeclaration)
                .map(TypeDeclaration::asClassOrInterfaceDeclaration)
                .findAny()
                .get();
        testDefinitionContext.setClassDeclaration(classDeclaration);

        Optional<SingleMemberAnnotationExpr> taskTestAnnotation = classDeclaration.getAnnotations().stream()
                .filter(annotationExpr -> annotationExpr.isSingleMemberAnnotationExpr()
                        && TaskTest.class.getSimpleName().equals(annotationExpr.getNameAsString()))
                .findAny()
                .map(Expression::asSingleMemberAnnotationExpr);
        if (taskTestAnnotation.isPresent()) {
            String codeTaskId = taskTestAnnotation.get().getMemberValue().toString();
            if (!Objects.equals("\"" + taskId + "\"", codeTaskId)) {
                throw new ParseException("Выбранный номер задачи не соответствует номеру задачи, указанному в аннотации TaskTest");
            }
        } else {
            classDeclaration.addSingleMemberAnnotation(TaskTest.class, "\"" + taskId + "\"");
            testDefinitionContext.setUpdatedCodeSource(LexicalPreservingPrinter.print(compilationUnit));
        }
    }

    private TestDefinitionCompilationInfo createCompilationInfo(CompileCodeResult compileCodeResult) {
        return new TestDefinitionCompilationInfo(compileCodeResult.getStatus(),
                compileCodeResult.getErrors(), compileCodeResult.getWarnings());
    }

    private void backupPreviousTestVersion(TestDefinitionContext testDefinitionContext) {
        String fullyQualifiedPath = AstUtils.getFullyQualifiedName(testDefinitionContext.getClassDeclaration())
                .replace(PKG_SEPARATOR, DIR_SEPARATOR);

        String targetJavaPathStr = testClassesFolder + DIR_SEPARATOR + fullyQualifiedPath + JavaFileObject.Kind.SOURCE.extension;
        Path targetJavaPath = Paths.get(targetJavaPathStr);
        testDefinitionContext.setTestFolderJavaFilePath(targetJavaPath);

        String targetClassPathStr = testClassesFolder + DIR_SEPARATOR + fullyQualifiedPath + JavaFileObject.Kind.CLASS.extension;
        Path targetClassPath = Paths.get(targetClassPathStr);
        testDefinitionContext.setTestFolderClassFilePath(targetClassPath);

        String datetime = dateTimeFormatter.format(LocalDateTime.now());
        if (Files.exists(targetJavaPath)) {
            moveObsoleteFile(fullyQualifiedPath, datetime, targetJavaPath);
        }

        if (Files.exists(targetClassPath)) {
            moveObsoleteFile(fullyQualifiedPath, datetime, targetClassPath);
        }
    }

    private void moveObsoleteFile(String fullyQualifiedPath, String datetime, Path sourcePath) {
        String backupFileName = sourcePath.getFileName().toString() + BACKUP_EXTENSION + datetime;
        Path backJavaPath = Paths.get(backupTestClassesFolder + DIR_SEPARATOR + fullyQualifiedPath).resolveSibling(backupFileName);
        try {
            Files.createDirectories(backJavaPath);
            Files.move(sourcePath,  backJavaPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyFilesToTestFolderPath(TestDefinitionContext testDefinitionContext) {
        Path javaFilePath = testDefinitionContext.getJavaFilePath();
        try {
            Files.createDirectories(testDefinitionContext.getTestFolderJavaFilePath().getParent());
            Files.copy(javaFilePath, testDefinitionContext.getTestFolderJavaFilePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path classFilePath = Paths.get(testDefinitionContext.getJavaFilePath().toString()
                .replace(JavaFileObject.Kind.SOURCE.extension, JavaFileObject.Kind.CLASS.extension));
        try {
            Files.copy(classFilePath, testDefinitionContext.getTestFolderClassFilePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
