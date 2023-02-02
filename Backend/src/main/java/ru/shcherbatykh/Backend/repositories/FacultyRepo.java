package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Faculty;

@Repository
public interface FacultyRepo extends CrudRepository<Faculty, Long> {
}
