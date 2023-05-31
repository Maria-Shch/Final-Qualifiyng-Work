package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseRepeatedParamsAndSequence {
    private boolean repeatedSerialNumber;
    private boolean repeatedName;
    private boolean sequenceRight;
}
