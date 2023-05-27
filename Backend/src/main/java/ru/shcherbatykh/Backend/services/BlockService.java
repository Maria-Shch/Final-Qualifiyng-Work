package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.NumberingPair;
import ru.shcherbatykh.Backend.dto.RequestUpdateNumbering;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.repositories.BlockRepo;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class BlockService {
    private final BlockRepo blockRepo;
    private final ChapterService chapterService;

    public BlockService(BlockRepo blockRepo, ChapterService chapterService) {
        this.blockRepo = blockRepo;
        this.chapterService = chapterService;
    }

    public List<Block> getSortedBlocksOfChapterWithoutTheory(int serialNumberOfChapter) {
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

    public boolean checkIsPresentNameOfBlock(Block newBlock) {
        return blockRepo.findBlockByChapterAndName(newBlock.getChapter(), newBlock.getName())!=null;
    }

    public Block getBlockById(long blockId) {
        return blockRepo.findById(blockId).get();
    }

    public Block updateBlock(Block updatedBlock) {
        Block block = blockRepo.findById(updatedBlock.getId()).get();
        block.setName(updatedBlock.getName());
        if (!Objects.equals(block.getChapter().getId(), updatedBlock.getChapter().getId())){
            Chapter oldChapter = block.getChapter();
            int oldSerialNumber = block.getSerialNumber();
            int newSerialNumber = getCountOfBlocks(updatedBlock.getChapter().getSerialNumber())+1;
            block.setSerialNumber(newSerialNumber);
            block.setChapter(updatedBlock.getChapter());
            block = blockRepo.save(block);

            if (oldSerialNumber - getCountOfBlocks(oldChapter.getSerialNumber()) != 1){
                if (getCountOfBlocks(oldChapter.getSerialNumber()) != 0){
                    for (int i = oldSerialNumber + 1; i <= getCountOfBlocks(oldChapter.getSerialNumber())+1; i++) {
                        Block b = getBlock(oldChapter, i);
                        int currSerialNumber = b.getSerialNumber();
                        b.setSerialNumber(currSerialNumber - 1);
                        blockRepo.save(b);
                    }
                }
            }
        }
        return block;
    }

    public boolean updateBlocksNumbering(RequestUpdateNumbering request) {
        for(NumberingPair pair: request.getNumberingPairs()){
            Block block = blockRepo.findById(pair.getObjId()).get();
            block.setSerialNumber(pair.getNewSerialNumber());
            blockRepo.save(block);
        }
        return true;
    }
}
