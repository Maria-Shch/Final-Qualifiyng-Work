package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shcherbatykh.application.models.Chapter;

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
