package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.dto.StudentProgress;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.services.StudentTaskService;
import ru.shcherbatykh.Backend.services.TaskService;
import ru.shcherbatykh.Backend.services.UserService;

@RestController
public class StudentTaskController {
    private final StudentTaskService studentTaskService;
    public final UserService userService;
    private final TaskService taskService;

    public StudentTaskController(StudentTaskService studentTaskService, UserService userService, TaskService taskService) {
        this.studentTaskService = studentTaskService;
        this.userService = userService;
        this.taskService = taskService;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/student/{userId}")
    public StudentTask getStudentTask(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                      @PathVariable int serialNumberOfTask, @PathVariable long userId) {
        User user = userService.findById(userId).orElse(null);
        Task task = taskService.getTask(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask);
        if (user == null || task == null) return null;
        else {
            return studentTaskService.getStudentTask(user, task);
        }
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/student/{userId}/progress")
    public StudentProgress getStudentProgress( @PathVariable long userId) {
        User user = userService.findById(userId).orElse(null);
        return studentTaskService.getStudentProgress(user);
    }
}
