package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.RequestType;
import ru.shcherbatykh.Backend.repositories.RequestTypeRepo;

@Service
public class RequestTypeService {
    private final RequestTypeRepo requestTypeRepo;

    public RequestTypeService(RequestTypeRepo requestTypeRepo) {
        this.requestTypeRepo = requestTypeRepo;
    }

    public RequestType getTypeOnReview(){
        return requestTypeRepo.findByName("На проверку");
    }

    public RequestType getTypeOnConsideration(){
        return requestTypeRepo.findByName("На рассмотрение");
    }
}
