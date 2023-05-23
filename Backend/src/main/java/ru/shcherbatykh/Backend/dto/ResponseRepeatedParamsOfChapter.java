package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseRepeatedParamsOfChapter {
    private boolean repeatedSerialNumber;
    private boolean repeatedName;
}
