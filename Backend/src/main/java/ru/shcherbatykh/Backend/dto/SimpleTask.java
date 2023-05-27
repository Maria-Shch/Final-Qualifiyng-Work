package ru.shcherbatykh.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimpleTask {
    Long taskId;
    int serialNumber;
    String fullname;
}
