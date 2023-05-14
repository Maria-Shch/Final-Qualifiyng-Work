package ru.shcherbatykh.autochecker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class CompilationError {
    private long lineNumber;
    private long position;
    private String message;
}
