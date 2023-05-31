package ru.shcherbatykh.common.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestDefinitionRequest {
    private String taskId;
    private String requestUuid;
    private String codeSource;
}
