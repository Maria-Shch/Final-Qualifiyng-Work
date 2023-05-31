package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StudentProgress {
    private List<ChapterAndStatInfo> chapterAndStatInfoList;
}
