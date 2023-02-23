package ru.shcherbatykh.Backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.services.BlockService;
import ru.shcherbatykh.Backend.services.ChapterService;

import java.util.List;

@RestController
public class CollectionOfTasksController {
    private final ChapterService chapterService;
    private final BlockService blockService;

    public CollectionOfTasksController(ChapterService chapterService, BlockService blockService) {
        this.chapterService = chapterService;
        this.blockService = blockService;
    }

    @GetMapping("/chapter/all")
    public List<Chapter> getChapters(){
        return chapterService.getChaptersSortBySerialNumber();
    }

    @GetMapping("/chapters/count")
    public long getCountOfChapters(){
        return chapterService.getCountOfChapters();
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/blocks")
    public List<Block> getBlocksOfChapter(@PathVariable int serialNumberOfChapter){
        return blockService.getBlocksOfChapter(serialNumberOfChapter);
    }
}