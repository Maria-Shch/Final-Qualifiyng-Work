package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Faculty;

import java.util.List;

@Repository
public interface FacultyRepo extends CrudRepository<Faculty, Long> {
    List<Faculty> findAll(Sort sort);
}
