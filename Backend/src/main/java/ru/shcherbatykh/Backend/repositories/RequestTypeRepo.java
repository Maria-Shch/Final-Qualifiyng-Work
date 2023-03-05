package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.RequestType;

@Repository
public interface RequestTypeRepo extends CrudRepository<RequestType, Long> {
    RequestType findByName(String name);
}
