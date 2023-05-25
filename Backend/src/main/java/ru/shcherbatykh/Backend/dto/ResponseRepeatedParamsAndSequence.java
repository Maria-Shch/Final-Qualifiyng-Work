package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseRepeatedParamsAndSequence {
    private boolean repeatedSerialNumber;
    private boolean repeatedName;
    private boolean sequenceRight;
}
