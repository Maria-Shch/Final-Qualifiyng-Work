package ru.shcherbatykh.Backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.ResponseRepeatedParamsOfChapter;
import ru.shcherbatykh.Backend.models.Block;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.services.BlockService;
import ru.shcherbatykh.Backend.services.ChapterService;

import java.util.List;

@RestController
public class ChapterAndBlockController {

    private final ChapterService chapterService;
    private final BlockService blockService;

    public ChapterAndBlockController(ChapterService chapterService, BlockService blockService) {
        this.chapterService = chapterService;
        this.blockService = blockService;
    }

    @GetMapping("/chapter/all")
    public List<Chapter> getChapters(){
        return chapterService.getChaptersSortBySerialNumber();
    }

    @GetMapping("/chapters/count")
    public long getCountOfChapters(){
        return chapterService.getCountOfChapters();
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

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/create/chapter")
    public Chapter createNewChapter(@RequestBody Chapter newChapter) {
        return chapterService.createNewChapter(newChapter);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/check/chapter")
    public ResponseRepeatedParamsOfChapter checkIsPresentNameOrSerialNumberOfChapter(@RequestBody Chapter newChapter) {
        return chapterService.checkIsPresentNameOrSerialNumberOfChapter(newChapter);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/chapter/{chapterId}")
    public Chapter getChapterById(@PathVariable long chapterId){
        return chapterService.getChapterById(chapterId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/update/chapter")
    public Chapter updateChapter(@RequestBody Chapter updatedChapter) {
        return chapterService.updateChapter(updatedChapter);
    }
}
