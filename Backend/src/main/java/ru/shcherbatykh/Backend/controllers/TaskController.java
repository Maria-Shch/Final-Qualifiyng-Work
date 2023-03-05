package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.RBOnConsideration;
import ru.shcherbatykh.Backend.dto.SendingOnReviewOrConsiderationResponse;
import ru.shcherbatykh.Backend.dto.TaskOfBlock;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.ChapterService;
import ru.shcherbatykh.Backend.services.TaskService;

import java.util.List;

@RestController
public class TaskController {

    private final ChapterService chapterService;
    private final AuthService authService;
    private final TaskService taskService;

    public TaskController(ChapterService chapterService,AuthService authService, TaskService taskService) {
        this.chapterService = chapterService;
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

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/practice")
    public List<TaskOfBlock> getPractice(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return taskService.getPracticeForNoAuthUser(serialNumberOfChapter, serialNumberOfBlock);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/practice")
    public List<TaskOfBlock> getPracticeForAuthUSer(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return taskService.getPracticeForAuthUser(serialNumberOfChapter, serialNumberOfBlock, authService.getUser().orElse(null));
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/tasks/count")
    public int getCountOfTasks(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock){
        return taskService.getCountOfTasks(serialNumberOfChapter, serialNumberOfBlock);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}")
    public Task getTask(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                @PathVariable int serialNumberOfTask){
        return taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/saveDescription")
    public Task saveDescriptionOfTask(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
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

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/getClasses")
    public List<String> getClasses(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                   @PathVariable int serialNumberOfTask) {
        return taskService.getClassesForTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null));
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/onReview")
    public SendingOnReviewOrConsiderationResponse onReview(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                                           @PathVariable int serialNumberOfTask, @RequestBody List<String> codes) {
        return taskService.sendTaskOnReview(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null), codes);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/onConsideration")
    public SendingOnReviewOrConsiderationResponse onConsideration(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                                                  @PathVariable int serialNumberOfTask,
                                                                  @RequestBody RBOnConsideration rbOnConsideration) {
        return taskService.sendTaskOnConsideration(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null), rbOnConsideration);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/cancelReview")
    public boolean cancelReview(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                   @PathVariable int serialNumberOfTask) {
        return taskService.cancelReview(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null));
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/cancelConsideration")
    public boolean cancelConsideration(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                     @PathVariable int serialNumberOfTask) {
        return taskService.cancelConsideration(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null));
    }
}