package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.NumberingPair;
import ru.shcherbatykh.Backend.dto.RequestUpdateNumbering;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.repositories.BlockRepo;

import java.util.ArrayList;
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
        for (Block block : blocksByChapter) block.setTextTheory(null);
        return blocksByChapter.stream()
                .sorted(Comparator.comparing(Block::getSerialNumber))
                .toList();
    }

    public Block getBlock(Chapter chapter, int serialNumberOfBlock) {
        return blockRepo.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
    }

    public Block getBlock(int serialNumberOfChapter, int serialNumberOfBlock) {
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return blockRepo.getBlockByChapterAndSerialNumber(chapter, serialNumberOfBlock);
    }

    public int getCountOfBlocks(int serialNumberOfChapter) {
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        return blockRepo.countByChapter(chapter);
    }

    public Block saveTextOfTheory(int serialNumberOfChapter, int serialNumberOfBlock, String textOfTheory) {
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        Block block = getBlock(chapter, serialNumberOfBlock);
        block.setTextTheory(textOfTheory);
        return saveBlock(block);
    }

    public Block saveBlock(Block block) {
        return blockRepo.save(block);
    }

    public Block getLastBlockOfChapter(int serialNumberOfChapter) {
        Chapter chapter = chapterService.getChapterBySerialNumber(serialNumberOfChapter);
        int countOfBlocks = getCountOfBlocks(chapter.getSerialNumber());
        if (countOfBlocks != 0){
            return getBlock(chapter.getSerialNumber(), countOfBlocks);
        } else {
            return null;
        }
    }

    public Block getPreviousBlock(int sNOfChapter, int sNOfBlock) {
        if (sNOfBlock == 1 && sNOfChapter == 1) {
            return null;
        } else if (sNOfBlock != 1) {
            return getBlock(sNOfChapter, sNOfBlock - 1);
        } else if (sNOfChapter != 1 && sNOfBlock == 1) {
            Block lastBlockOfChapter;
            for (int i = 1; i < sNOfChapter; i++) {
                lastBlockOfChapter = getLastBlockOfChapter(sNOfChapter - i);
                if (lastBlockOfChapter != null) {
                    return lastBlockOfChapter;
                }
            }
        }
        return null;
    }


    public Block getNextBlock(int sNOfChapter, int sNOfBlock) {
        if (sNOfBlock == getLastBlockOfChapter(sNOfChapter).getSerialNumber()) {
            if (sNOfChapter == chapterService.getCountOfChapters()) {
                return null;
            } else {
                Block block;
                for (int i = sNOfChapter + 1; i <= chapterService.getCountOfChapters(); i++) {
                    block = getBlock(i, 1);
                    if (block != null) {
                        return block;
                    }
                }
            }
        } else {
            return getBlock(sNOfChapter, sNOfBlock + 1);
        }
        return null;
    }

    public Block createNewBlock(Block newBlock) {
        return blockRepo.save(newBlock);
    }

    public boolean checkIsPresentNameOfBlock(Block newBlock) {
        return blockRepo.findBlockByChapterAndName(newBlock.getChapter(), newBlock.getName()) != null;
    }

    public Block getBlockById(long blockId) {
        return blockRepo.findById(blockId).get();
    }

    public Block updateBlock(Block updatedBlock) {
        Block block = blockRepo.findById(updatedBlock.getId()).get();
        block.setName(updatedBlock.getName());
        if (!Objects.equals(block.getChapter().getId(), updatedBlock.getChapter().getId())) {
            Chapter oldChapter = block.getChapter();
            int oldSerialNumber = block.getSerialNumber();
            int newSerialNumber = getCountOfBlocks(updatedBlock.getChapter().getSerialNumber()) + 1;
            block.setSerialNumber(newSerialNumber);
            block.setChapter(updatedBlock.getChapter());
            block = blockRepo.save(block);

            if (oldSerialNumber - getCountOfBlocks(oldChapter.getSerialNumber()) != 1) {
                if (getCountOfBlocks(oldChapter.getSerialNumber()) != 0) {
                    for (int i = oldSerialNumber + 1; i <= getCountOfBlocks(oldChapter.getSerialNumber()) + 1; i++) {
                        Block b = getBlock(oldChapter, i);
                        int currSerialNumber = b.getSerialNumber();
                        b.setSerialNumber(currSerialNumber - 1);
                        blockRepo.save(b);
                    }
                }
            }
        } else {
            block = blockRepo.save(block);
        }
        return block;
    }

    public boolean updateBlocksNumbering(RequestUpdateNumbering request) {
        for (NumberingPair pair : request.getNumberingPairs()) {
            Block block = blockRepo.findById(pair.getObjId()).get();
            block.setSerialNumber(pair.getNewSerialNumber());
            blockRepo.save(block);
        }
        return true;
    }

    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        List<Chapter> chaptersSortBySerialNumber = chapterService.getChaptersSortBySerialNumber();
        for (Chapter chapter : chaptersSortBySerialNumber) {
            blocks.addAll(getSortedBlocksOfChapterWithoutTheory(chapter.getSerialNumber()));
        }
        return blocks;
    }
}
