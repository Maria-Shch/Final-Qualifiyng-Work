package ru.shcherbatykh.application.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.Status;
import ru.shcherbatykh.application.repositories.StatusRepo;

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
