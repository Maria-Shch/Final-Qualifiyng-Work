package ru.shcherbatykh.Backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.BlockService;
import ru.shcherbatykh.Backend.services.ChapterService;
import ru.shcherbatykh.Backend.services.TaskService;

import java.util.List;

@RestController
public class CollectionOfTasksController {

    private final ChapterService chapterService;
    private final BlockService blockService;
    private final AuthService authService;
    private final TaskService taskService;

    public CollectionOfTasksController(ChapterService chapterService, BlockService blockService, AuthService authService, TaskService taskService) {
        this.chapterService = chapterService;
        this.blockService = blockService;
        this.authService = authService;
        this.taskService = taskService;
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
        return blockService.getBlocksOfChapterWithoutTheory(serialNumberOfChapter);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/practice")
    public List<TaskOfBlock> getPractice(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return taskService.getPracticeForNoAuthUser(serialNumberOfChapter, serialNumberOfBlock);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/practice")
    public List<TaskOfBlock> getPracticeForAuthUSer(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return taskService.getPracticeForAuthUser(serialNumberOfChapter, serialNumberOfBlock, authService.getUser().orElse(null));
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}")
    public ResponseEntity<String> getNameOfBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return ResponseEntity.ok(blockService.getBlockBySNOfChapterAndSNOfBlock(serialNumberOfChapter, serialNumberOfBlock).getName());
    }

    @GetMapping("/chapters/{serialNumberOfChapter}/blocks/count")
    public long getCountOfBlocks(@PathVariable int serialNumberOfChapter){
        return blockService.getCountOfBlocks(serialNumberOfChapter);
    }
}