package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.ClosingStatus;

@Repository
public interface ClosingStatusRepo extends CrudRepository<ClosingStatus, Long> {
    ClosingStatus findByName(String name);
}
