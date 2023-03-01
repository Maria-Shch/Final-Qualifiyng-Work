package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.repositories.BlockRepo;

import java.util.Comparator;
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
        return blocksByChapter.stream()
                .sorted(Comparator.comparing(Block::getSerialNumber))
                .toList();
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

    public Block saveTextOfTheory(int serialNumberOfChapter, int serialNumberOfBlock, String textOfTheory){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        Block block = getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
        block.setTextTheory(textOfTheory);
        return saveBlock(block);
    }

    public Block saveBlock(Block block){
        return blockRepo.save(block);
    }

    public Block getLastBlockOfChapter(int serialNumberOfChapter){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return getBlockBySNOfChapterAndSNOfBlock(chapter.getSerialNumber(), getCountOfBlocks(chapter.getSerialNumber()));
    }
}
