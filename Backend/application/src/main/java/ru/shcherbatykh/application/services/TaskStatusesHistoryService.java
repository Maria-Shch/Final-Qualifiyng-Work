package ru.shcherbatykh.application.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.Status;
import ru.shcherbatykh.application.models.StudentTask;
import ru.shcherbatykh.application.models.TaskStatusHistory;
import ru.shcherbatykh.application.repositories.TaskStatusHistoryRepo;

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
