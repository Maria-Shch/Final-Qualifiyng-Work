package ru.shcherbatykh.common.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeCheckResponse {
    private String studentId;
    private String taskId;
    private String requestUuid;
    private String code;
    private String message;
    private List<CodeTestResult> results;
}
