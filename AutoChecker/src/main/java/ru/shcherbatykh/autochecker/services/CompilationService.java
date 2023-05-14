package ru.shcherbatykh.autochecker.services;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.autochecker.model.Status;
import ru.shcherbatykh.autochecker.model.results.CompileCodeResult;

import javax.tools.*;
import java.nio.file.Path;
import java.util.Collection;

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
        task.call();
        fileManager.close();

        CompileCodeResult compileCodeResult = new CompileCodeResult(diagnosticCollector.getDiagnostics().isEmpty()
                ? Status.OK : Status.NOK);
        diagnosticCollector.getDiagnostics().forEach(compileCodeResult::addDiagnostic);
        return compileCodeResult;
    }
}
