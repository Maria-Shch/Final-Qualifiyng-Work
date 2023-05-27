package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.*;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.TaskService;

import java.util.List;

@RestController
public class TaskController {

    private final AuthService authService;
    private final TaskService taskService;

    public TaskController(AuthService authService, TaskService taskService) {
        this.authService = authService;
        this.taskService = taskService;
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

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/task/{taskId}/manualCheck/{manualCheckValue}")
    public boolean setManualCheckValue(@PathVariable Long taskId, @PathVariable boolean manualCheckValue) {
        return taskService.setManualCheckValue(taskId, manualCheckValue);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/tasks/count")
    public long getCountOfBlocks(@PathVariable int serialNumberOfChapter,
                                 @PathVariable int serialNumberOfBlock){
        return taskService.getCountOfTasks(serialNumberOfChapter, serialNumberOfBlock);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/check/task")
    public boolean checkIsPresentNameOfTask(@RequestBody Task newTask) {
        return taskService.checkIsPresentNameOfTask(newTask);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/create/task")
    public Task createNewTask(@RequestBody NewTask newTask) {
        return taskService.createNewTask(newTask);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/simpleTaskCollection")
    public SimpleCollection getSimpleTaskCollection() {
        return taskService.getSimpleTaskCollection();
    }
}