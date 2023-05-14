package ru.shcherbatykh.Backend.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Year;
import ru.shcherbatykh.Backend.repositories.YearRepo;

import java.util.List;
import java.util.Optional;

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

    public Year addNewYear(Year newYear) {
        return yearRepo.save(newYear);
    }

    public Optional<Year> findByName(String name) {
        return yearRepo.findByName(name);
    }
}
