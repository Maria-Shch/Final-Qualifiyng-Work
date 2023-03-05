package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.TaskStatusHistory;
import ru.shcherbatykh.Backend.repositories.TaskStatusHistoryRepo;

@Service
public class TaskStatusesHistoryService {
    private final TaskStatusHistoryRepo taskStatusHistoryRepo;

    public TaskStatusesHistoryService(TaskStatusHistoryRepo taskStatusHistoryRepo) {
        this.taskStatusHistoryRepo = taskStatusHistoryRepo;
    }

    public void registerStatusChange(StudentTask studentTask, Status oldStatus, Status newStatus){
        taskStatusHistoryRepo.save(new TaskStatusHistory(studentTask, oldStatus, newStatus));
    }
}
