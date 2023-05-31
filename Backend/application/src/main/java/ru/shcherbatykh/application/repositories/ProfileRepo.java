package ru.shcherbatykh.application.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.Profile;

import java.util.List;

@Repository
public interface ProfileRepo extends CrudRepository<Profile, Long> {
    List<Profile> findAll(Sort sort);
}
