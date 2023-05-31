package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.RequestType;

import java.util.List;

@Repository
public interface RequestTypeRepo extends CrudRepository<RequestType, Long> {
    RequestType findByName(String name);
    List<RequestType> findAll();
}
