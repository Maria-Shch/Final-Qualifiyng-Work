package ru.shcherbatykh.Backend.classes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeTestResult {
    private Status status;
    private CodeTestType type;
    private JsonNode result;

    public static final CodeTestResult OK_AST_RESULT = CodeTestResult.builder()
            .status(Status.OK)
            .type(CodeTestType.AST)
            .build();

    public static final CodeTestResult OK_COMPILATION_RESULT = CodeTestResult.builder()
            .status(Status.OK)
            .type(CodeTestType.COMPILE)
            .build();

    public static final CodeTestResult OK_REFLEXIVITY_RESULT = CodeTestResult.builder()
            .status(Status.OK)
            .type(CodeTestType.REFLEXIVITY)
            .build();
}
