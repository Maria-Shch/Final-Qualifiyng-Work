package ru.shcherbatykh.Backend.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Year;
import ru.shcherbatykh.Backend.repositories.YearRepo;

import java.util.List;

@Service
public class YearService {
    private final YearRepo yearRepo;

    public YearService(YearRepo yearRepo) {
        this.yearRepo = yearRepo;
    }

    public List<Year> getYearsSorted() {
        return yearRepo.findAll(orderByNameAsc());
    }

    private Sort orderByNameAsc() {
        return Sort.by(Sort.Direction.ASC, "name");
    }
}
