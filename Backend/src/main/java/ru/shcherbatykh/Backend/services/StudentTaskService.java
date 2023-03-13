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

    public StudentTaskService(StudentTaskRepo studentTaskRepo, StatusService statusService, 
                              TaskStatusesHistoryService taskStatusesHistoryService) {
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

    public StudentTask findById(Long id){
       return studentTaskRepo.findById(id).orElse(null);
    }

    public StudentTask addNew(User user, Task task){
        return studentTaskRepo.save(new StudentTask(user, task, statusService.getStatusByName("Не решена")));
    }

    public Status getStatusByUserAndTask(User user, Task task){
        Optional<StudentTask> studentTask = studentTaskRepo.findStudentTaskByUserAndTask(user, task);
        if (studentTask.isPresent()) return studentTask.get().getCurrStatus();
        else return statusService.getStatusByName("Не решена");
    }

    public void setStatusAndWriteHistory(StudentTask sT, String status){
        Status oldStatus = sT.getCurrStatus();
        Status newStatus = statusService.getStatusByName(status);
        sT.setCurrStatus(newStatus);
        studentTaskRepo.save(sT);
        taskStatusesHistoryService.registerStatusChange(sT, oldStatus, newStatus);
    }

    public void setStatusNotSolved(StudentTask sT){
        setStatusAndWriteHistory(sT, "Не решена");
    }

    public void setStatusOnReview(StudentTask sT){
        setStatusAndWriteHistory(sT, "На проверке");
    }

    public void setStatusRejected(StudentTask sT){
        setStatusAndWriteHistory(sT, "Возвращена преподавателем");
    }

    public void setStatusSolved(StudentTask sT){
        setStatusAndWriteHistory(sT, "Решена");
    }

    public void setStatusOnTesting(StudentTask sT){
        setStatusAndWriteHistory(sT, "На тестировании");
    }

    public void setStatusNotPassedTests(StudentTask sT){
        setStatusAndWriteHistory(sT, "Не прошла тесты");
    }

    public void setStatusPassedTests(StudentTask sT){
        setStatusAndWriteHistory(sT, "Прошла тесты");
    }

    public void setStatusOnConsideration(StudentTask sT){
        setStatusAndWriteHistory(sT, "На рассмотрении");
    }
}
