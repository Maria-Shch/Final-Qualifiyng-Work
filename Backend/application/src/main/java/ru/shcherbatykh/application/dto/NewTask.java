package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.models.Task;

import java.util.List;

@Getter
@Setter
public class NewTask {
    Task task;
    List<Long> selectedPreviousTaskIds;
}
