package ru.shcherbatykh.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.models.Task;
import ru.shcherbatykh.application.models.User;

@Getter
@Setter
@AllArgsConstructor
public class UserStatInfo {
    private User user;
    private Task lastSolvedTask;
    private int countOfSolvedTasks;
}
