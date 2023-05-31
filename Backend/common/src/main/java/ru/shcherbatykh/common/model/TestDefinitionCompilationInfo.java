package ru.shcherbatykh.common.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestDefinitionCompilationInfo {
    private Status status;
    private List<CompilationDiagnostic> errors;
    private List<CompilationDiagnostic> warnings;
}
