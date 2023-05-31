package ru.shcherbatykh.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;

@Getter
@Setter
@AllArgsConstructor
public class UserStatInfo {
    private User user;
    private Task lastSolvedTask;
    private int countOfSolvedTasks;
}
