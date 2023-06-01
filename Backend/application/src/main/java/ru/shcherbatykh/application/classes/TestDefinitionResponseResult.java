package ru.shcherbatykh.application.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shcherbatykh.common.model.TestDefinitionCompilationInfo;
import ru.shcherbatykh.common.model.TestDefinitionTechnicalErrorInfo;
import ru.shcherbatykh.common.model.TestDefinitionValidationInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDefinitionResponseResult {
    private String code;
    private String message;
    private String codeTest;
    private TestDefinitionValidationInfo validationInfo;
    private TestDefinitionCompilationInfo compilationInfo;
    private TestDefinitionTechnicalErrorInfo technicalErrorInfo;
}
