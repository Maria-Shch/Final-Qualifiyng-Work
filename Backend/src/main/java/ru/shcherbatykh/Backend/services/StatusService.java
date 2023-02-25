package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Status;
import ru.shcherbatykh.Backend.repositories.StatusRepo;

@Service
public class StatusService {
    private final StatusRepo statusRepo;

    public StatusService(StatusRepo statusRepo) {
        this.statusRepo = statusRepo;
    }

    public Status getStatusByName(String name){
        return statusRepo.findStatusByName(name);
    }
}
