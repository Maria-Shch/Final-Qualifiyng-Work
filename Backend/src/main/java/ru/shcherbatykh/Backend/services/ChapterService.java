package ru.shcherbatykh.Backend.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.dto.ResponseRepeatedParamsOfChapter;
import ru.shcherbatykh.Backend.models.Chapter;
import ru.shcherbatykh.Backend.repositories.ChapterRepo;

import java.util.List;

@Service
public class ChapterService {
    private final ChapterRepo chapterRepo;

    public ChapterService(ChapterRepo chapterRepo) {
        this.chapterRepo = chapterRepo;
    }

    public List<Chapter> getChaptersSortBySerialNumber(){
        return chapterRepo.findAll(orderBySerialNumber());
    }

    private Sort orderBySerialNumber() {
        return Sort.by(Sort.Direction.ASC, "serialNumber");
    }

    public long getCountOfChapters(){
        return chapterRepo.count();
    }

    public Chapter getChapterBySerialNumber(int serialNumber){
        return chapterRepo.findChapterBySerialNumber(serialNumber);
    }

    public Chapter createNewChapter(Chapter newChapter) {
        return chapterRepo.save(newChapter);
    }

    public ResponseRepeatedParamsOfChapter checkIsPresentNameOrSerialNumberOfChapter(Chapter newChapter) {
        ResponseRepeatedParamsOfChapter response = new ResponseRepeatedParamsOfChapter();
        response.setRepeatedSerialNumber(chapterRepo.findChapterBySerialNumber(newChapter.getSerialNumber())!=null);
        response.setRepeatedName(chapterRepo.findChapterByName(newChapter.getName())!=null);
        return response;
    }

    public Chapter getChapterById(long chapterId) {
        return chapterRepo.findById(chapterId).orElse(null);
    }

    public Chapter updateChapter(Chapter updatedChapter) {
        return chapterRepo.save(updatedChapter);
    }
}
