package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.RequestState;

@Repository
public interface RequestStateRepo extends CrudRepository<RequestState, Long> {
    RequestState findByName(String name);
}
