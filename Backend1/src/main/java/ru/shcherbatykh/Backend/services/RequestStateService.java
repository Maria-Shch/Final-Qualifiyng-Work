package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.RequestState;
import ru.shcherbatykh.Backend.repositories.RequestStateRepo;

import java.util.List;

@Service
public class RequestStateService {
    private final RequestStateRepo requestStateRepo;

    public RequestStateService(RequestStateRepo requestStateRepo) {
        this.requestStateRepo = requestStateRepo;
    }

    public List<RequestState> findAll(){
        return requestStateRepo.findAll();
    }

    public RequestState getRSNotSeen(){
        return requestStateRepo.findByName("Не просмотрен");
    }

    public RequestState getRSSeen(){
        return requestStateRepo.findByName("Просмотрен");
    }

    public RequestState getRSProcessed(){
        return requestStateRepo.findByName("Обработан");
    }

    public RequestState getRSNCanceled(){
        return requestStateRepo.findByName("Отменён");
    }
}
