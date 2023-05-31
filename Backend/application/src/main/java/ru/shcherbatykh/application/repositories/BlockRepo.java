package ru.shcherbatykh.application.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.shcherbatykh.application.models.Block;
import ru.shcherbatykh.application.models.Chapter;

import java.util.List;

@Repository
public interface BlockRepo extends CrudRepository<Block, Long> {
    List<Block> getBlocksByChapter(Chapter chapter);

    Block getBlockByChapterAndSerialNumber(Chapter chapter, int serialNumber);

    int countByChapter(Chapter chapter);

    Block findBlockByChapterAndName(Chapter chapter, String name);
}
