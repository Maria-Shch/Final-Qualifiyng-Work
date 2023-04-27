package ru.shcherbatykh.Backend.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.LevelOfEdu;
import ru.shcherbatykh.Backend.repositories.LevelOfEduRepo;

import java.util.List;

@Service
public class LevelOfEduService {
    private final LevelOfEduRepo levelOfEduRepo;

    public LevelOfEduService(LevelOfEduRepo levelOfEduRepo) {
        this.levelOfEduRepo = levelOfEduRepo;
    }

    public List<LevelOfEdu> getLevelsOfEduSorted(){
        return levelOfEduRepo.findAll(orderByName());
    }

    private Sort orderByName() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
