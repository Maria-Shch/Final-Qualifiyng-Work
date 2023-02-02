package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.EventType;

@Repository
public interface EventTypeRepo extends CrudRepository<EventType, Long> {
}
