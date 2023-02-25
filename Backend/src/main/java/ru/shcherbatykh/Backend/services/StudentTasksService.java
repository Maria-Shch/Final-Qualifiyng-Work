package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.StudentTaskRepo;

import java.util.Optional;

@Service
public class StudentTasksService {
    private final StudentTaskRepo studentTaskRepo;
    private final StatusService statusService;

    public StudentTasksService(StudentTaskRepo studentTaskRepo, StatusService statusService) {
        this.studentTaskRepo = studentTaskRepo;
        this.statusService = statusService;
    }

    public Status getStatusByUserAndTask(User user, Task task){
        Optional<StudentTask> studentTask = studentTaskRepo.findStudentTaskByUserAndTask(user, task);
        if (studentTask.isPresent()) return studentTask.get().getCurrStatus();
        else return statusService.getStatusByName("Не решена");
    }
}
