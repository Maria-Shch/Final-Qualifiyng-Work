package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.ResponseRepeatedParamsAndSequence;
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

    public Block getBlock(Chapter chapter, int serialNumberOfBlock){
        return blockRepo.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
    }

    public Block getBlock(int serialNumberOfChapter, int serialNumberOfBlock){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return blockRepo.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
    }

    public int getCountOfBlocks(int serialNumberOfChapter) {
        Chapter chapter =  chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return blockRepo.countByChapter(chapter);
    }

    public Block saveTextOfTheory(int serialNumberOfChapter, int serialNumberOfBlock, String textOfTheory){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        Block block = getBlock(chapter, serialNumberOfBlock);
        block.setTextTheory(textOfTheory);
        return saveBlock(block);
    }

    public Block saveBlock(Block block){
        return blockRepo.save(block);
    }

    public Block getLastBlockOfChapter(int serialNumberOfChapter){
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return getBlock(chapter.getSerialNumber(), getCountOfBlocks(chapter.getSerialNumber()));
    }

    public Block getPreviousBlock(int sNOfChapter, int sNOfBlock) {
        if (sNOfBlock == 1 && sNOfChapter == 1) return null;
        else if (sNOfBlock != 1) {
            return getBlock(sNOfChapter, sNOfBlock - 1);
        }
        else if (sNOfChapter != 1 && sNOfBlock == 1) {
            int sNOfLastBlock = getLastBlockOfChapter(sNOfChapter - 1).getSerialNumber();
            return getBlock (sNOfChapter - 1, sNOfLastBlock);
        }
        return null;
    }


    public Block getNextBlock(int sNOfChapter, int sNOfBlock) {
        if (sNOfBlock == getLastBlockOfChapter(sNOfChapter).getSerialNumber()){
            if (sNOfChapter == chapterService.getCountOfChapters()){
                return null;
            }
            else return getBlock(sNOfChapter + 1, 1);
        } else return getBlock(sNOfChapter, sNOfBlock + 1);
    }

    public Block createNewBlock(Block newBlock) {
        return blockRepo.save(newBlock);
    }

    public ResponseRepeatedParamsAndSequence checkIsPresentNameOrSerialNumberOfBlock(Block newBlock) {
        ResponseRepeatedParamsAndSequence response = new ResponseRepeatedParamsAndSequence();
        response.setRepeatedSerialNumber(blockRepo.findBlockByChapterAndSerialNumber(newBlock.getChapter(), newBlock.getSerialNumber())!=null);
        response.setRepeatedName(blockRepo.findBlockByChapterAndName(newBlock.getChapter(), newBlock.getName())!=null);
        return response;
    }

}
