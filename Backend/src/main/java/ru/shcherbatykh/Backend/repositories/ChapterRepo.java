package ru.shcherbatykh.Backend.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.Backend.models.Chapter;

import java.util.List;

@Repository
public interface ChapterRepo extends CrudRepository<Chapter, Long> {
    List<Chapter> findAll(Sort sort);
    Chapter findChapterBySerialNumber(int serialNumber);
    long count();
}
