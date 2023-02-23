package ru.shcherbatykh.Backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.services.ChapterService;

import java.util.List;

@RestController
public class CollectionOfTasksController {
    private final ChapterService chapterService;

    public CollectionOfTasksController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/chapter/all")
    public List<Chapter> getChapters(){
        return chapterService.getChaptersSortBySerialNumber();
    }
}