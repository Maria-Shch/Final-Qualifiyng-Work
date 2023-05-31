package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.EventType;
import ru.shcherbatykh.Backend.repositories.EventTypeRepo;

@Service
public class EventTypeService {
    private final EventTypeRepo eventTypeRepo;

    public EventTypeService(EventTypeRepo eventTypeRepo) {
        this.eventTypeRepo = eventTypeRepo;
    }

    public EventType getETStudentSendOnReview(){
        return eventTypeRepo.findByName("Студент отправил на проверку");
    }

    public EventType getETStudentSendOnConsideration(){
        return eventTypeRepo.findByName("Студент отправил на рассмотрение");
    }

    public EventType getETTeacherAccepted(){
        return eventTypeRepo.findByName("Преподаватель принял решение");
    }

    public EventType getETTeacherRejected(){
        return eventTypeRepo.findByName("Преподаватель отклонил решение");
    }

    public EventType getETStudentCanceled(){
        return eventTypeRepo.findByName("Студент отменил запрос");
    }
}
