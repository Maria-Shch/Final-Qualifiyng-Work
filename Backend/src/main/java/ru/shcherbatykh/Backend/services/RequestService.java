package ru.shcherbatykh.Backend.services;

import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.Request;
import ru.shcherbatykh.Backend.models.StudentTask;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.repositories.RequestRepo;

import java.time.LocalDateTime;

@Service
public class RequestService {
    private final RequestRepo requestRepo;
    private final UserService userService;
    private final RequestTypeService requestTypeService;
    private final RequestStateService requestStateService;
    private final EventHistoryService eventHistoryService;

    public RequestService(RequestRepo requestRepo, UserService userService, RequestTypeService requestTypeService,
                          RequestStateService requestStateService, EventHistoryService eventHistoryService) {
        this.requestRepo = requestRepo;
        this.userService = userService;
        this.requestTypeService = requestTypeService;
        this.requestStateService = requestStateService;
        this.eventHistoryService = eventHistoryService;
    }

    public void createRequestOnReview(StudentTask stTask){
        User teacher = userService.getTeacher(stTask);
        Request request = new Request(stTask, teacher,
                requestTypeService.getTypeOnReview(), requestStateService.getRSNotSeen());
        requestRepo.save(request);
        eventHistoryService.registerEventStudentSendOnReview(request);
    }

    public void createRequestOnConsideration(StudentTask stTask, String studentMsg){
        User teacher = userService.getTeacher(stTask);
        Request request = new Request(stTask, teacher,
                requestTypeService.getTypeOnConsideration(), requestStateService.getRSNotSeen(), studentMsg);
        requestRepo.save(request);
        eventHistoryService.registerEventStudentSendOnConsideration(request);
    }

    public void cancelRequest(StudentTask stTask){
        Request request = getOpenRequest(stTask);
        request.setRequestState(requestStateService.getRSNCanceled());
        request.setClosingTime( LocalDateTime.now());
        requestRepo.save(request);
        eventHistoryService.registerEventUserCanceledRequest(request);
    }

    public Request getOpenRequest(StudentTask stTask){
        return requestRepo.getRequestByStudentTaskAndClosingTime(stTask, null);
    }
}
