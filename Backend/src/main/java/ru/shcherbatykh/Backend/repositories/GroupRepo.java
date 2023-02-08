package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Group;

import java.util.List;

@Repository
public interface GroupRepo extends CrudRepository<Group, Long> {
    List<Group> findAll();
}
