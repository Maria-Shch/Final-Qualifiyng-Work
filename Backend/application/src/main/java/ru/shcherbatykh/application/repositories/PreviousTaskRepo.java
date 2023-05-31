package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.PreviousTask;

@Repository
public interface PreviousTaskRepo extends CrudRepository<PreviousTask, Long> {
}
