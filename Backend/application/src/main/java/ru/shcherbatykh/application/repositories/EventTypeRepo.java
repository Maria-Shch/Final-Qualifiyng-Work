package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.EventType;

@Repository
public interface EventTypeRepo extends CrudRepository<EventType, Long> {
    EventType findByName(String name);
}
