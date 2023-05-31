package ru.shcherbatykh.common.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestDefinitionValidationInfo {
    private List<String> errors;
}
