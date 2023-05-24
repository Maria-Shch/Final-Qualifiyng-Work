package ru.shcherbatykh.Backend.classes;

import lombok.*;
import ru.shcherbatykh.autochecker.model.CodeSource;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CodeCheckRequest {
    private String studentId;
    private String taskId;
    private String taskPath;
    private String requestUuid;
    private List<CodeSource> codeSources;
}