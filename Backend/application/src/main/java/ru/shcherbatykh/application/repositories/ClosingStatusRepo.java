package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.ClosingStatus;

@Repository
public interface ClosingStatusRepo extends CrudRepository<ClosingStatus, Long> {
    ClosingStatus findByName(String name);
}
