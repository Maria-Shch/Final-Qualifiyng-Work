package ru.shcherbatykh.application.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.Faculty;
import ru.shcherbatykh.application.repositories.FacultyRepo;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {
    private final FacultyRepo facultyRepo;

    public FacultyService(FacultyRepo facultyRepo) {
        this.facultyRepo = facultyRepo;
    }

    public List<Faculty> getFacultiesSorted(){
        return facultyRepo.findAll(orderByNameAsc());
    }

    private Sort orderByNameAsc() {
        return Sort.by(Sort.Direction.ASC, "name");
    }

    public Faculty addNewFaculty(Faculty newFaculty) {
        return facultyRepo.save(newFaculty);
    }

    public Optional<Faculty> findByName(String name) {
        return facultyRepo.findByName(name);
    }
}
