package ru.shcherbatykh.autochecker.model;

import lombok.*;
import ru.shcherbatykh.Backend.classes.CodeTestResult;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeCheckResponse {
    private String studentId;
    private String taskId;
    private String taskPath;
    private String requestUuid;
    private String code;
    private String message;
    private List<CodeTestResult> results;
}
