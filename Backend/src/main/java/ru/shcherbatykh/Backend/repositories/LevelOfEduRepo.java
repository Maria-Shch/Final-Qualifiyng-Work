package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.LevelOfEdu;

@Repository
public interface LevelOfEduRepo extends CrudRepository<LevelOfEdu, Long> {
}
