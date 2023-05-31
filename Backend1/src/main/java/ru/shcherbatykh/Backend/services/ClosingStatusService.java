package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.ClosingStatus;
import ru.shcherbatykh.Backend.repositories.ClosingStatusRepo;

@Service
public class ClosingStatusService {
    private final ClosingStatusRepo closingStatusRepo;

    public ClosingStatusService(ClosingStatusRepo closingStatusRepo) {
        this.closingStatusRepo = closingStatusRepo;
    }

    public ClosingStatus getCSSolutionAccepted(){
        return closingStatusRepo.findByName("Решение принято");
    }

    public ClosingStatus getCSSolutionRejected(){
        return closingStatusRepo.findByName("Решение отклонено");
    }
}
