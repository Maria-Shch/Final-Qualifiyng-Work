package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shcherbatykh.application.models.Block;
import ru.shcherbatykh.application.models.StudentTask;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BlockAndStatInfo {
    private Block block;
    private int countOfSolvedTasks;
    private int countOfAllTasks;
    private List<StudentTask> studentTaskList;
}
