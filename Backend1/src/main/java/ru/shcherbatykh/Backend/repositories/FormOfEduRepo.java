package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.FormOfEdu;

import java.util.List;

@Repository
public interface FormOfEduRepo extends CrudRepository<FormOfEdu, Long> {
    List<FormOfEdu> findAll(Sort sort);
}
