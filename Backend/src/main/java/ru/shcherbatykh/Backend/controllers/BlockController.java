package ru.shcherbatykh.Backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.services.BlockService;

import java.util.List;

@RestController
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/blocks")
    public List<Block> getBlocksOfChapter(@PathVariable int serialNumberOfChapter){
        return blockService.getBlocksOfChapterWithoutTheory(serialNumberOfChapter);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/name")
    public ResponseEntity<String> getNameOfBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return ResponseEntity.ok(blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock).getName());
    }

    @GetMapping("/chapters/{serialNumberOfChapter}/blocks/count")
    public long getCountOfBlocks(@PathVariable int serialNumberOfChapter){
        return blockService.getCountOfBlocks(serialNumberOfChapter);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}")
    public Block getBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock){
        return blockService.getBlock(serialNumberOfChapter, serialNumberOfBlock);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/previousBlock")
    public Block getPreviousBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return blockService.getPreviousBlock(serialNumberOfChapter, serialNumberOfBlock);
    }

    @GetMapping("/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/nextBlock")
    public Block getNextBlock(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock) {
        return blockService.getNextBlock(serialNumberOfChapter, serialNumberOfBlock);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/saveTheory")
    public Block saveTextOfTheory(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                  @RequestBody String textOfTheory) {
        return blockService.saveTextOfTheory(serialNumberOfChapter, serialNumberOfBlock, textOfTheory);
    }
}
