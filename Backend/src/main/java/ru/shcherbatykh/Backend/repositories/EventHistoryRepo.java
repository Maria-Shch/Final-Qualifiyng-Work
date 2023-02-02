package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.EventHistory;

@Repository
public interface EventHistoryRepo extends CrudRepository<EventHistory, Long> {
}
