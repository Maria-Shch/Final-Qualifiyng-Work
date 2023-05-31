package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.RequestType;
import ru.shcherbatykh.Backend.repositories.RequestTypeRepo;

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
