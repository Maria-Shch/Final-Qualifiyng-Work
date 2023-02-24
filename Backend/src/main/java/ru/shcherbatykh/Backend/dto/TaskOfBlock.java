package ru.shcherbatykh.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.models.Status;

@Getter
@Setter
@AllArgsConstructor
public class TaskOfBlock {
    Long id;
    int serialNumber;
    String name;
    Status status;
}
