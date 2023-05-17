package ru.shcherbatykh.autochecker.services;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FileWriterService {
    @Value("${autochecker.source_files_folder}")
    private String sourceFilesFolder;

    public void saveCompilationSources(CodeCheckContext codeCheckContext) throws ParseException {
        Path requestPath = Paths.get(sourceFilesFolder, codeCheckContext.getStudentId(), codeCheckContext.getTaskPath(),
                codeCheckContext.getRequestUuid());
        if (Files.exists(requestPath)) {
            throw new RuntimeException("Duplicate event for request UUID " + codeCheckContext.getRequestUuid());
        }
        codeCheckContext.setRequestPath(requestPath);

        Set<Path> createdJavaFiles = new HashSet<>();
        Map<CompilationUnit, Path> javaFilePaths = new HashMap<>();
        for (int i = 0; i < codeCheckContext.getCompilationUnits().size(); i++) {
            CompilationUnit compilationUnit = codeCheckContext.getCompilationUnits().get(i);
            String javaPackage = StringUtils.EMPTY;
            Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
            if (packageDeclaration.isPresent()) {
                javaPackage = packageDeclaration.get().getNameAsString();
            }
            javaPackage = javaPackage.replaceAll("\\.", "/");

            TypeDeclaration<?> clazz = getHeadTypeInCompilationUnit(compilationUnit);
            Path javaFile;
            try {
                javaFile = createJavaFile(requestPath, javaPackage, clazz.getNameAsString(),
                        codeCheckContext.getPlainCodeSources().get(i), createdJavaFiles);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            javaFilePaths.put(compilationUnit, javaFile);
        }

        codeCheckContext.setJavaFilePaths(javaFilePaths);
    }

    private TypeDeclaration getHeadTypeInCompilationUnit(CompilationUnit compilationUnit) throws ParseException {
        List<TypeDeclaration> publicTypes = compilationUnit.getChildNodes().stream()
                .filter(TypeDeclaration.class::isInstance)
                .map(TypeDeclaration.class::cast)
                .filter(TypeDeclaration::isPublic)
                .toList();
        if (publicTypes.isEmpty()) {
            Optional<TypeDeclaration> type = compilationUnit.getChildNodes().stream()
                    .filter(TypeDeclaration.class::isInstance)
                    .map(TypeDeclaration.class::cast)
                    .findAny();
            if (type.isEmpty()) {
                throw new ParseException("По крайней мере один тип (class/interface/enum/record/annotation) " +
                        "должен существовать в одной единице компиляции");
            }
            return type.get();
        }

        if (publicTypes.size() > 1) {
            throw new ParseException("Только один публичный тип (class/interface/enum/record/annotation) может " +
                    "находиться в одной единице компиляции");
        }
        return publicTypes.iterator().next();
    }

    private Path createJavaFile(Path requestPath, String javaPackage, String className, String classBody,
                                Set<Path> createdJavaFiles) throws ParseException, IOException {
        Path packagePath = Paths.get(requestPath.toString(), javaPackage);
        if (!Files.exists(packagePath)) {
            Files.createDirectories(packagePath);
        }

        Path filePath = Paths.get(packagePath.toString(), className + ".java");
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