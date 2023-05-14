package ru.shcherbatykh.autochecker.services;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.model.CodeCheckContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileWriterService {
    @Value("${autochecker.source_files_folder}")
    private String sourceFilesFolder;

    public void saveCompilationSources(CodeCheckContext codeCheckContext) throws ParseException {
        Path requestPath = Paths.get(sourceFilesFolder, codeCheckContext.getStudentId(), codeCheckContext.getTaskId(),
                codeCheckContext.getRequestUuid());
        if (Files.exists(requestPath)) {
            throw new RuntimeException("Duplicate event for request UUID " + codeCheckContext.getRequestUuid());
        }
        codeCheckContext.setRequestPath(requestPath);

        List<Path> javaFilePaths = new ArrayList<>();
        for (int i = 0; i < codeCheckContext.getCompilationUnits().size(); i++) {
            CompilationUnit compilationUnit = codeCheckContext.getCompilationUnits().get(i);
            String javaPackage = StringUtils.EMPTY;
            Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
            if (packageDeclaration.isPresent()) {
                javaPackage = packageDeclaration.get().getNameAsString();
            }
            javaPackage = javaPackage.replaceAll("\\.", "/");

            TypeDeclaration<?> clazz = getHeadTypeInCompilationUnit(compilationUnit);
            Path javaFile = createJavaFile(requestPath, javaPackage, clazz.getNameAsString(),
                    codeCheckContext.getPlainCodeSources().get(i));
            javaFilePaths.add(javaFile);
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
                throw new ParseException("At least one type (annotation, interface, class or record) must be placed in compilation unit");
            }
            return type.get();
        }

        if (publicTypes.size() > 1) {
            throw new ParseException("Only one public type (annotation, interface, class or record) may be placed within one compilation unit");
        }
        return publicTypes.iterator().next();
    }

    @SneakyThrows
    private Path createJavaFile(Path requestPath, String javaPackage, String className, String classBody) {
        Path packagePath = Paths.get(requestPath.toString(), javaPackage);
        if (!Files.exists(packagePath)) {
            Files.createDirectories(packagePath);
        }

        Path filePath = Paths.get(packagePath.toString(), className + ".java");
        Files.createFile(filePath);
        return Files.writeString(filePath, classBody);
    }
}
