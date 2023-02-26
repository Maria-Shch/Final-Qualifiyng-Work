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

    public List<Block> getBlocksOfChapterWithoutTheory(int serialNumberOfChapter) {
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        List<Block> blocksByChapter = blockRepo.getBlocksByChapter(chapter);
        for(Block block: blocksByChapter) block.setTextTheory(null);
        return blocksByChapter;
    }

    public Block getBlockByChapterAndSerialNumber(Chapter chapter, int serialNumberOfBlock){
        return blockRepo.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
    }

    public Block getBlockBySNOfChapterAndSNOfBlock(int serialNumberOfChapter, int serialNumberOfBlock){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return blockRepo.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
    }

    public int getCountOfBlocks(int serialNumberOfChapter) {
        Chapter chapter =  chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return blockRepo.countByChapter(chapter);
    }
}
