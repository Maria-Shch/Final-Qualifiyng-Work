package ru.shcherbatykh.autochecker.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeCheckResponse {
    private String code;
    private String message;
    private List<CodeTestResult> results;
}
