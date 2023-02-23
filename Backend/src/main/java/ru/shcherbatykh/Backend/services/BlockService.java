package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.repositories.BlockRepo;

import java.util.List;

@Service
public class BlockService {
    private final BlockRepo blockRepo;
    private final ChapterService chapterService;

    public BlockService(BlockRepo blockRepo, ChapterService chapterService) {
        this.blockRepo = blockRepo;
        this.chapterService = chapterService;
    }


    public List<Block> getBlocksOfChapter(int serialNumberOfChapter) {
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return blockRepo.getBlocksByChapter(chapter);
    }
}
