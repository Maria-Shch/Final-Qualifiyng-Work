package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Chapter;

@Repository
public interface ChapterRepo extends CrudRepository<Chapter, Long> {
}
