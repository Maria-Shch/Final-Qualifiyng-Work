package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Year;

import java.util.List;

@Repository
public interface YearRepo extends CrudRepository<Year, Long> {
    List<Year> findAll(Sort sort);
}
