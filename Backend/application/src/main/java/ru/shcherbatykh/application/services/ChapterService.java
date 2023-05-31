package ru.shcherbatykh.application.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.dto.NumberingPair;
import ru.shcherbatykh.application.dto.RequestUpdateNumbering;
import ru.shcherbatykh.application.models.Chapter;
import ru.shcherbatykh.application.repositories.ChapterRepo;

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

    public boolean checkIsPresentNameOfChapter(Chapter newChapter) {
        return chapterRepo.findChapterByName(newChapter.getName())!=null;
    }

    public Chapter getChapterById(long chapterId) {
        return chapterRepo.findById(chapterId).orElse(null);
    }

    public Chapter updateChapter(Chapter updatedChapter) {
        return chapterRepo.save(updatedChapter);
    }

    public boolean updateChaptersNumbering(RequestUpdateNumbering request) {
        for(NumberingPair pair: request.getNumberingPairs()){
            Chapter chapter = chapterRepo.findById(pair.getObjId()).get();
            chapter.setSerialNumber(pair.getNewSerialNumber());
            chapterRepo.save(chapter);
        }
        return true;
    }
}
