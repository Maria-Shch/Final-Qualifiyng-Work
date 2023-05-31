package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.models.Task;

import java.util.List;

@Getter
@Setter
public class NewTask {
    Task task;
    List<Long> selectedPreviousTaskIds;
}
