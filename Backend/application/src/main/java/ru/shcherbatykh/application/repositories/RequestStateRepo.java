package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.RequestState;

import java.util.List;

@Repository
public interface RequestStateRepo extends CrudRepository<RequestState, Long> {
    RequestState findByName(String name);
    List<RequestState> findAll();
}
