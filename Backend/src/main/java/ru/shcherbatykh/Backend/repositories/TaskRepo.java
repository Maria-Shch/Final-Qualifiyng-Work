package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Task;

import java.util.List;

@Repository
public interface TaskRepo extends CrudRepository<Task, Long> {
    List<Task> getTaskByBlock(Block block);
}
