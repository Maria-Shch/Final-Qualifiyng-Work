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

    public StudentTask getStudentTask(User user, Task task){
        return studentTaskRepo.findStudentTaskByUserAndTask(user, task).orElse(null);
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
        sT.setCurrStatus(statusService.getStatusByName("Не решена"));
        studentTaskRepo.save(sT);
    }

    public void setStatusOnReview(StudentTask sT){
        sT.setCurrStatus(statusService.getStatusByName("На проверке"));
        studentTaskRepo.save(sT);
    }

    public void setStatusReturned(StudentTask sT){
        sT.setCurrStatus(statusService.getStatusByName("Возвращена преподавателем"));
        studentTaskRepo.save(sT);
    }

    public void setStatusSolved(StudentTask sT){
        sT.setCurrStatus(statusService.getStatusByName("Решена"));
        studentTaskRepo.save(sT);
    }

    public void setStatusOnTesting(StudentTask sT){
        sT.setCurrStatus(statusService.getStatusByName("На тестировании"));
        studentTaskRepo.save(sT);
    }

    public void setStatusNotPassedTests(StudentTask sT){
        sT.setCurrStatus(statusService.getStatusByName("Не прошла тесты"));
        studentTaskRepo.save(sT);
    }

    public void setStatusPassedTests(StudentTask sT){
        sT.setCurrStatus(statusService.getStatusByName("Прошла тесты"));
        studentTaskRepo.save(sT);
    }

    public void setStatusOnConsideration(StudentTask sT){
        sT.setCurrStatus(statusService.getStatusByName("На рассмотрении"));
        studentTaskRepo.save(sT);
    }
}
