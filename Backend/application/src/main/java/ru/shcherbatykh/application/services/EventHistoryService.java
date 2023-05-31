package ru.shcherbatykh.application.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.application.models.EventHistory;
import ru.shcherbatykh.application.models.Request;
import ru.shcherbatykh.application.models.StudentTask;
import ru.shcherbatykh.application.models.User;
import ru.shcherbatykh.application.repositories.EventHistoryRepo;

import javax.persistence.criteria.Join;
import java.util.List;

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

    public List<EventHistory> getHistoryByUserAndPageNumber(User user, int pageNumber) {
        Pageable sortedByTimeDesc = PageRequest.of(pageNumber, 5, Sort.by("time").descending());
        return eventHistoryRepo.findAll(getSpecificationForGettingHistoryByUser(user.getId()), sortedByTimeDesc);
    }

    private Specification<EventHistory> getSpecificationForGettingHistoryByUser(Long userId){
        return (root, query, criteriaBuilder) -> {
            Join<Request, EventHistory> request = root.join("request");
            Join<StudentTask, Join<Request, EventHistory>> stTask = request.join("studentTask");
            return criteriaBuilder.equal(stTask.get("user"), userId) ;
        };
    }
}
