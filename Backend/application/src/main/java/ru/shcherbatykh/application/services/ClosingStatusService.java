package ru.shcherbatykh.application.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.ClosingStatus;
import ru.shcherbatykh.application.repositories.ClosingStatusRepo;

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
