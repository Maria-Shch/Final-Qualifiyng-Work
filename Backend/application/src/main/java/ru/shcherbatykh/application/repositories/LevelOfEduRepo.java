package ru.shcherbatykh.application.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.LevelOfEdu;

import java.util.List;

@Repository
public interface LevelOfEduRepo extends CrudRepository<LevelOfEdu, Long> {
    List<LevelOfEdu> findAll(Sort sort);
}
