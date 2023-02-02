package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Profile;

@Repository
public interface ProfileRepo extends CrudRepository<Profile, Long> {
}
