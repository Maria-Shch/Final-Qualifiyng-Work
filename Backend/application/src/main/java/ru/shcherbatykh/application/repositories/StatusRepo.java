package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.Status;

@Repository
public interface StatusRepo extends CrudRepository<Status, Long> {
    Status findStatusByName(String name);
}
