package ru.shcherbatykh.application.classes;

import lombok.*;
import ru.shcherbatykh.common.model.CodeTestResult;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeCheckResponseResult {
    //enum ResponseCode = code + message
    private String code;
    private String message;
    private List<CodeTestResult> results;
}
