package ru.shcherbatykh.autochecker.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.List;

@Getter
@Builder
public class CodeCheckContext {
    private final String studentId;
    private final String taskId;
    private final String requestUuid;
    private final List<String> plainCodeSources;
    private final List<CompilationUnit> compilationUnits;
    private final CompilationUnit mainUnit;
    private final ClassOrInterfaceDeclaration mainClass;
    private final MethodDeclaration mainMethod;

    @Setter
    private Path requestPath;

    @Setter
    private List<Path> javaFilePaths;

    @Setter
    private JavaParserFacade javaParserFacade;

    @Setter
    private boolean compilationPassed;
}
