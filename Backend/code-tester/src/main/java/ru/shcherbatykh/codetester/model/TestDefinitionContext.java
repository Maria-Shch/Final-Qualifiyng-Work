package ru.shcherbatykh.codetester.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Builder
public class TestDefinitionContext {
    private final String taskId;
    private final String requestUuid;
    private final String codeSource;
    private final CompilationUnit compilationUnit;

    @Setter
    private ClassOrInterfaceDeclaration classDeclaration;

    @Setter
    private String updatedCodeSource;

    @Setter
    private Path requestPath;

    @Setter
    private Path javaFilePath;

    @Setter
    private Path testFolderJavaFilePath;

    @Setter
    private Path testFolderClassFilePath;
}
