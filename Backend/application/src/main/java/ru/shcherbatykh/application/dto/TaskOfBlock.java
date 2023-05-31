package ru.shcherbatykh.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.models.Status;

@Getter
@Setter
@AllArgsConstructor
public class TaskOfBlock {
    Long id;
    int serialNumber;
    String name;
    Status status;
}
