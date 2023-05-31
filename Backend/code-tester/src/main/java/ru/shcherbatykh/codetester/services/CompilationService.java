package ru.shcherbatykh.codetester.services;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.codetester.model.results.CompileCodeResult;
import ru.shcherbatykh.common.model.Status;

import javax.tools.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@Service
public class CompilationService {
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    @SneakyThrows
    public CompileCodeResult compileJavaClasses(final Collection<Path> pathsToCompile) {
        StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromPaths(pathsToCompile);
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = COMPILER.getTask(
                null,
                fileManager,
                diagnosticCollector,
                null,
                null,
                compilationUnits
        );
        Boolean compilationResult = task.call();
        fileManager.close();

        List<Diagnostic<? extends JavaFileObject>> errors = diagnosticCollector.getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR || diagnostic.getKind() == Diagnostic.Kind.OTHER)
                .toList();
        List<Diagnostic<? extends JavaFileObject>> warnings = diagnosticCollector.getDiagnostics().stream()
                .filter(diagnostic -> diagnostic.getKind() != Diagnostic.Kind.ERROR && diagnostic.getKind() != Diagnostic.Kind.OTHER)
                .toList();
        CompileCodeResult compileCodeResult = new CompileCodeResult(Boolean.TRUE.equals(compilationResult)
                ? Status.OK : Status.NOK);
        errors.forEach(compileCodeResult::addError);
        warnings.forEach(compileCodeResult::addWarning);
        return compileCodeResult;
    }
}
