package ru.shcherbatykh.application.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.Faculty;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepo extends CrudRepository<Faculty, Long> {
    List<Faculty> findAll(Sort sort);

    Optional<Faculty> findByName(String name);
}
