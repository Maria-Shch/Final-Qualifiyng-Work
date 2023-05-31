package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.StudentTask;

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