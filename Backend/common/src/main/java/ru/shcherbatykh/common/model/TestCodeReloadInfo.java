package ru.shcherbatykh.common.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestCodeReloadInfo {
    private Status status;
    private List<String> errors;
    private List<String> warnings;
}
