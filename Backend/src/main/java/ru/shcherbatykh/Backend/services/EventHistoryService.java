package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.EventHistory;
import ru.shcherbatykh.Backend.models.Request;
import ru.shcherbatykh.Backend.repositories.EventHistoryRepo;

@Service
public class EventHistoryService {
    private final EventHistoryRepo eventHistoryRepo;
    private final EventTypeService eventTypeService;

    public EventHistoryService(EventHistoryRepo eventHistoryRepo, EventTypeService eventTypeService) {
        this.eventHistoryRepo = eventHistoryRepo;
        this.eventTypeService = eventTypeService;
    }

    public void registerEventStudentSendOnReview(Request request){
        EventHistory eventHistory = new EventHistory(eventTypeService.getETStudentSendOnReview(), request);
        eventHistoryRepo.save(eventHistory);
    }

    public void registerEventStudentSendOnConsideration(Request request){
        EventHistory eventHistory = new EventHistory(eventTypeService.getETStudentSendOnConsideration(), request);
        eventHistoryRepo.save(eventHistory);
    }

    public void registerEventTeacherAccepted(Request request){
        EventHistory eventHistory = new EventHistory(eventTypeService.getETTeacherAccepted(), request);
        eventHistoryRepo.save(eventHistory);
    }

    public void registerEventTeacherRejected(Request request){
        EventHistory eventHistory = new EventHistory(eventTypeService.getETTeacherRejected(), request);
        eventHistoryRepo.save(eventHistory);
    }

    public void registerEventUserCanceledRequest(Request request){
        EventHistory eventHistory = new EventHistory(eventTypeService.getETStudentCanceled(), request);
        eventHistoryRepo.save(eventHistory);
    }
}
