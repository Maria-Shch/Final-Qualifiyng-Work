package ru.shcherbatykh.autochecker.model.results;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.shcherbatykh.autochecker.model.CompilationError;
import ru.shcherbatykh.autochecker.model.Status;

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
    private List<CompilationError> errors;

    public CompileCodeResult(Status status) {
        this.status = status;
        if (status == Status.NOK) {
            errors = new ArrayList<>();
        } else {
            errors = null;
        }
    }

    public void addDiagnostic(Diagnostic<?> diagnostic) {
        if (errors != null) {
            errors.add(new CompilationError(diagnostic.getLineNumber(), diagnostic.getColumnNumber(),
                    diagnostic.getMessage(Locale.getDefault())));
        }
    }
}
