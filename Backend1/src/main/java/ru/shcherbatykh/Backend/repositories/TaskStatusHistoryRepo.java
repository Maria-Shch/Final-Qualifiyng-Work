package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.TaskStatusHistory;

@Repository
public interface TaskStatusHistoryRepo extends CrudRepository<TaskStatusHistory, Long> {
}
