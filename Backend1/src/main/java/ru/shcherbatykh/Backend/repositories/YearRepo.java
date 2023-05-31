package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Year;

import java.util.List;
import java.util.Optional;

@Repository
public interface YearRepo extends CrudRepository<Year, Long> {
    List<Year> findAll(Sort sort);

    Optional<Year> findByName(String name);
}
