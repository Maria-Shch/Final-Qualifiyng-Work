package ru.shcherbatykh.autochecker.model.results;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RunCodeResult extends Result {
    private String expectedValue;
    private String actualValue;
    private String error;
}
