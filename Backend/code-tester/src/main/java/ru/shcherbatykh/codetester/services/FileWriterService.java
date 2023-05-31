package ru.shcherbatykh.codetester.services;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.codetester.model.CodeCheckContext;
import ru.shcherbatykh.codetester.model.TestDefinitionContext;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileWriterService {
    @Value("${autochecker.code_tester.io.student_task_source_code_folder}")
    private String studentTaskSourceCodeFolder;

    @Value("${autochecker.hot_reload.io.test_source_code_folder}")
    private String testSourceCodeFolder;

    public void saveStudentCompilationSources(CodeCheckContext codeCheckContext) throws ParseException {
        Path requestPath = Paths.get(studentTaskSourceCodeFolder, codeCheckContext.getStudentId(),
                codeCheckContext.getTaskId(), codeCheckContext.getRequestUuid());
        validateRequestPath(requestPath, codeCheckContext.getRequestUuid());
        codeCheckContext.setRequestPath(requestPath);

        Set<Path> createdJavaFiles = new HashSet<>();
        Map<CompilationUnit, Path> javaFilePaths = new HashMap<>();
        for (int i = 0; i < codeCheckContext.getCompilationUnits().size(); i++) {
            CompilationUnit compilationUnit = codeCheckContext.getCompilationUnits().get(i);
            String codeSource = codeCheckContext.getPlainCodeSources().get(i);
            Path javaFile = saveJavaClass(requestPath, compilationUnit, codeSource, createdJavaFiles);
            javaFilePaths.put(compilationUnit, javaFile);
        }

        codeCheckContext.setJavaFilePaths(javaFilePaths);
    }

    public void saveCompilationUnit(TestDefinitionContext testDefinitionContext) throws ParseException {
        Path requestPath = Paths.get(testSourceCodeFolder, testDefinitionContext.getTaskId(),
                testDefinitionContext.getRequestUuid());
        validateRequestPath(requestPath, testDefinitionContext.getRequestUuid());
        testDefinitionContext.setRequestPath(requestPath);

        Set<Path> createdJavaFiles = new HashSet<>(1);
        String codeSource = StringUtils.isEmpty(testDefinitionContext.getUpdatedCodeSource())
                ? testDefinitionContext.getCodeSource()
                : testDefinitionContext.getUpdatedCodeSource();
        Path javaFile = saveJavaClass(requestPath, testDefinitionContext.getCompilationUnit(),
                codeSource, createdJavaFiles);
        testDefinitionContext.setJavaFilePath(javaFile);
    }

    private static void validateRequestPath(Path requestPath, String requestUuid) {
        if (Files.exists(requestPath)) {
            throw new RuntimeException("Duplicate event for request UUID " + requestUuid);
        }
    }

    private String getJavaPackage(CompilationUnit compilationUnit) {
        String javaPackage = StringUtils.EMPTY;
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        if (packageDeclaration.isPresent()) {
            javaPackage = packageDeclaration.get().getNameAsString();
        }
        javaPackage = javaPackage.replaceAll("\\.", "/");
        return javaPackage;
    }

    private TypeDeclaration<?> getHeadTypeInCompilationUnit(CompilationUnit compilationUnit) throws ParseException {
        List<TypeDeclaration<?>> topPublicTypeDeclarations = compilationUnit.getTypes().stream()
                .filter(TypeDeclaration::isTopLevelType)
                .filter(TypeDeclaration::isPublic)
                .toList();
        if (topPublicTypeDeclarations.isEmpty()) {
            Optional<TypeDeclaration<?>> type = compilationUnit.getTypes().stream()
                    .filter(TypeDeclaration::isTopLevelType)
                    .findAny();
            if (type.isEmpty()) {
                throw new ParseException("По крайней мере один тип (class/interface/enum/record/annotation) " +
                        "должен существовать в одной единице компиляции");
            }
            return type.get();
        }

        if (topPublicTypeDeclarations.size() > 1) {
            throw new ParseException("Только один публичный тип (class/interface/enum/record/annotation) может " +
                    "находиться в одной единице компиляции");
        }
        return topPublicTypeDeclarations.iterator().next();
    }

    private Path saveJavaClass(Path requestPath, CompilationUnit compilationUnit,
                               String codeSource, Set<Path> createdJavaFiles) throws ParseException {
        String javaPackage = getJavaPackage(compilationUnit);
        TypeDeclaration<?> clazz = getHeadTypeInCompilationUnit(compilationUnit);

        Path javaFile;
        try {
            javaFile = createJavaFile(requestPath, javaPackage, clazz.getNameAsString(),
                    codeSource, createdJavaFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return javaFile;
    }

    private Path createJavaFile(Path requestPath, String javaPackage, String className, String classBody,
                                Set<Path> createdJavaFiles) throws ParseException, IOException {
        Path packagePath = Paths.get(requestPath.toString(), javaPackage);
        if (!Files.exists(packagePath)) {
            Files.createDirectories(packagePath);
        }

        Path filePath = Paths.get(packagePath.toString(), className + JavaFileObject.Kind.SOURCE.extension);
        if (createdJavaFiles.contains(filePath)) {
            String fullClassName = javaPackage.replaceAll("/", ".");
            if (!fullClassName.isEmpty()) {
                fullClassName += ".";
            }
            fullClassName += className;
            throw new ParseException("Найдена повторяющаяся единица компиляции: " + fullClassName);
        }

        Files.createFile(filePath);
        createdJavaFiles.add(filePath);
        return Files.writeString(filePath, classBody);
    }
}
