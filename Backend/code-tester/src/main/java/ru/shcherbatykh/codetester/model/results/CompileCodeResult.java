package ru.shcherbatykh.codetester.model.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.shcherbatykh.common.model.CompilationDiagnostic;
import ru.shcherbatykh.common.model.Status;

import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompileCodeResult extends Result {
    public static final CompileCodeResult OK_RESPONSE = new CompileCodeResult(Status.OK);

    private Status status;
    private List<CompilationDiagnostic> errors;
    private List<CompilationDiagnostic> warnings;

    public CompileCodeResult(Status status) {
        this.status = status;
        if (status == Status.NOK) {
            errors = new ArrayList<>();
        } else {
            errors = null;
        }
        warnings = new ArrayList<>();
    }

    public void addError(Diagnostic<?> diagnostic) {
        if (errors != null) {
            errors.add(new CompilationDiagnostic(diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                    diagnostic.getMessage(Locale.getDefault())));
        }
    }

    public void addWarning(Diagnostic<?> diagnostic) {
        warnings.add(new CompilationDiagnostic(diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                diagnostic.getMessage(Locale.getDefault())));
    }
}
