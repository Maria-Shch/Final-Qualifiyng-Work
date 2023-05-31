package ru.shcherbatykh.common.model;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestDefinitionResponse {
    private String taskId;
    private String requestUuid;
    private String code;
    private String message;
    private TestDefinitionValidationInfo validationInfo;
    private TestDefinitionCompilationInfo compilationInfo;
    private TestDefinitionTechnicalErrorInfo technicalErrorInfo;
}
