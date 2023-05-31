package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shcherbatykh.Backend.models.Chapter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChapterAndStatInfo {
    private Chapter chapter;
    private int countOfSolvedTasks;
    private int countOfAllTasks;
    private List<BlockAndStatInfo> blockAndStatInfoList;
}
