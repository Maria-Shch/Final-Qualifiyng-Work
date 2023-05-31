package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Status;

@Repository
public interface StatusRepo extends CrudRepository<Status, Long> {
    Status findStatusByName(String name);
}