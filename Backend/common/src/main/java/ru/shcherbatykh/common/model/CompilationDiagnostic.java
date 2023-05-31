package ru.shcherbatykh.common.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDiagnostic {
    private long lineNumber;
    private long position;
    private String message;
}
