package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.Task;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.StudentTaskRepo;

import java.util.Optional;

@Service
public class StudentTaskService {
    private final StudentTaskRepo studentTaskRepo;
    private final StatusService statusService;
    private final TaskStatusesHistoryService taskStatusesHistoryService;

    public StudentTaskService(StudentTaskRepo studentTaskRepo, StatusService statusService, TaskStatusesHistoryService taskStatusesHistoryService) {
        this.studentTaskRepo = studentTaskRepo;
        this.statusService = statusService;
        this.taskStatusesHistoryService = taskStatusesHistoryService;
    }

    public StudentTask getStudentTask(User user, Task task){
      StudentTask stTask = studentTaskRepo.findStudentTaskByUserAndTask(user, task).orElse(null);
        if (stTask == null){
            stTask = addNew(user, task);
        }
        return stTask;
    }

    public StudentTask addNew(User user, Task task){
        return studentTaskRepo.save(new StudentTask(user, task, statusService.getStatusByName("Не решена")));
    }

    public Status getStatusByUserAndTask(User user, Task task){
        Optional<StudentTask> studentTask = studentTaskRepo.findStudentTaskByUserAndTask(user, task);
        if (studentTask.isPresent()) return studentTask.get().getCurrStatus();
        else return statusService.getStatusByName("Не решена");
    }

    public void setStatusNotSolved(StudentTask sT){
        Status oldStatus = sT.getCurrStatus();
        Status newStatus = statusService.getStatusByName("Не решена");
        sT.setCurrStatus(newStatus);
        studentTaskRepo.save(sT);
        taskStatusesHistoryService.registerStatusChange(sT, oldStatus, newStatus);
    }

    public void setStatus(StudentTask sT, String status){
        Status oldStatus = sT.getCurrStatus();
        Status newStatus = statusService.getStatusByName(status);
        sT.setCurrStatus(newStatus);
        studentTaskRepo.save(sT);
        taskStatusesHistoryService.registerStatusChange(sT, oldStatus, newStatus);
    }

    public void setStatusOnReview(StudentTask sT){
        setStatus(sT, "На проверке");
    }

    public void setStatusReturned(StudentTask sT){
        setStatus(sT, "Возвращена преподавателем");
    }

    public void setStatusSolved(StudentTask sT){
        setStatus(sT, "Решена");
    }

    public void setStatusOnTesting(StudentTask sT){
        setStatus(sT, "На тестировании");
    }

    public void setStatusNotPassedTests(StudentTask sT){
        setStatus(sT, "Не прошла тесты");
    }

    public void setStatusPassedTests(StudentTask sT){
        setStatus(sT, "Прошла тесты");
    }

    public void setStatusOnConsideration(StudentTask sT){
        setStatus(sT, "На рассмотрении");
    }
}
