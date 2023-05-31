package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.TaskStatusHistory;

@Repository
public interface TaskStatusHistoryRepo extends CrudRepository<TaskStatusHistory, Long> {
}
