package ru.shcherbatykh.Backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.ResponseAboutTestingAllowed;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.dto.TestingResultResponse;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.services.*;

import java.util.List;

@RestController
public class CollectionOfTasksController {

    private final ChapterService chapterService;
    private final BlockService blockService;
    private final AuthService authService;
    private final TaskService taskService;
    private final TestingService testingService;

    public CollectionOfTasksController(ChapterService chapterService, BlockService blockService, AuthService authService,
                                       TaskService taskService, TestingService testingService) {
        this.chapterService = chapterService;
        this.blockService = blockService;
        this.authService = authService;
        this.taskService = taskService;
        this.testingService = testingService;
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

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/name")
    public ResponseEntity<String> getNameOfBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return ResponseEntity.ok(blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock).getName());
    }

    @GetMapping("/chapters/{serialNumberOfChapter}/blocks/count")
    public long getCountOfBlocks(@PathVariable int serialNumberOfChapter){
        return blockService.getCountOfBlocks(serialNumberOfChapter);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/saveTheory")
    public Block saveTextOfTheory(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                              @RequestBody String textOfTheory) {
        return blockService.saveTextOfTheory(serialNumberOfChapter, serialNumberOfBlock, textOfTheory);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}")
    public Block getBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock){
        return blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/tasks/count")
    public int getCountOfTasks(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock){
        return taskService.getCountOfTasks(serialNumberOfChapter, serialNumberOfBlock);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}")
    public Task getCountOfTasks(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                @PathVariable int serialNumberOfTask){
        return taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/saveDescription")
    public Task saveTextOfTheory(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                 @PathVariable int serialNumberOfTask, @RequestBody String description) {
        return taskService.saveDescriptionOfTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask, description);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/status")
    public Status getStatusOfTask(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                  @PathVariable int serialNumberOfTask) {
        return taskService.getStatusOfTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null));
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/isTestingAllowed")
    public ResponseAboutTestingAllowed isTestingAllowed(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                                        @PathVariable int serialNumberOfTask) {
        ResponseAboutTestingAllowed responseAboutTestingAllowed = taskService.getResponseAboutTestingAllowed(
                serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask, authService.getUser().orElse(null));
        return responseAboutTestingAllowed;
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/previousTask")
    public Task getPreviousTask(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                @PathVariable int serialNumberOfTask) {
        return taskService.getPreviousTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/nextTask")
    public Task getNextTask(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                            @PathVariable int serialNumberOfTask) {
        return taskService.getNextTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/previousBlock")
    public Block getPreviousBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return blockService.getPreviousBlock(serialNumberOfChapter, serialNumberOfBlock);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/nextBlock")
    public Block getNextBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return blockService.getNextBlock(serialNumberOfChapter, serialNumberOfBlock);
    }
}