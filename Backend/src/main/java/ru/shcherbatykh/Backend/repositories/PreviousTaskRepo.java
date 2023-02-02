package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.PreviousTask;

@Repository
public interface PreviousTaskRepo extends CrudRepository<PreviousTask, Long> {
}
