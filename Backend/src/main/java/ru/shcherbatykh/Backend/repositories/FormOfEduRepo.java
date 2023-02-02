package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.FormOfEdu;

@Repository
public interface FormOfEduRepo extends CrudRepository<FormOfEdu, Long> {
}
