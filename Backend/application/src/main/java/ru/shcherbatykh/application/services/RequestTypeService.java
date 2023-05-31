package ru.shcherbatykh.application.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.RequestType;
import ru.shcherbatykh.application.repositories.RequestTypeRepo;

import java.util.List;

@Service
public class RequestTypeService {
    private final RequestTypeRepo requestTypeRepo;

    public RequestTypeService(RequestTypeRepo requestTypeRepo) {
        this.requestTypeRepo = requestTypeRepo;
    }

    public List<RequestType> findAll(){
        return requestTypeRepo.findAll();
    }

    public RequestType getTypeOnReview(){
        return requestTypeRepo.findByName("На проверку");
    }

    public RequestType getTypeOnConsideration(){
        return requestTypeRepo.findByName("На рассмотрение");
    }
}
