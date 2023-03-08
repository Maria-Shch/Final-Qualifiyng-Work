package ru.shcherbatykh.Backend.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.shcherbatykh.Backend.dto.Filter;
import ru.shcherbatykh.Backend.models.*;
import ru.shcherbatykh.Backend.repositories.RequestRepo;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public long getCountOfRequestsByTeacher(User teacher){
        return requestRepo.countAllByTeacher(teacher);
    }

    public List<Request> getRequestsByTeacherAndPageNumber(User teacher, int pageNumber) {
        Pageable sortedByCreationTimeDesc =
                PageRequest.of(pageNumber, 10, Sort.by("creationTime").descending());
        return requestRepo.findAllByTeacher(teacher, sortedByCreationTimeDesc);
    }

    public List<Request> setToNullSomeFields(List<Request> requests){
        for(Request request: requests){
            request.getStudentTask().getTask().setDescription(null);
            request.getStudentTask().getTask().getBlock().setTextTheory(null);
            request.getStudentTask().getUser().setPassword(null);
            request.getStudentTask().getUser().getGroup().setTeacher(null);
        }
        return requests;
    }

    public int getCountRequestsAfterFiltering(User teacher, Filter filter) {
        return requestRepo.count(getSpecification(teacher, filter));
    }

    public List<Request> getRequestsByPageNumberAndFilter(User teacher, int pageNumber, Filter filter) {
        Pageable sortedByCreationTimeDesc =
                PageRequest.of(pageNumber, 10, Sort.by("creationTime").descending());
        return requestRepo.findAll(getSpecification(teacher, filter), sortedByCreationTimeDesc);
    }

    public Specification<Request> getSpecification(User teacher, Filter filter) {

        Specification<Request> specification = hasTeacher(teacher.getId());

        if (!CollectionUtils.isEmpty(filter.getRequestStateIds())) {
            specification = specification.and(hasRequestStates(filter.getRequestStateIds()));
        }

        if (!CollectionUtils.isEmpty(filter.getRequestTypeIds())) {
            specification = specification.and(hasRequestTypes(filter.getRequestTypeIds()));
        }

        if (!CollectionUtils.isEmpty(filter.getGroupIds())) {
            specification = specification.and(hasGroupIds(filter.getGroupIds()));
        }

        return specification;
    }

    private Specification<Request> hasGroupIds(List<Long> groupIds) {
        return (root, query, criteriaBuilder) -> {
            Join<StudentTask, Request> studentTask = root.join("studentTask");
            Join<User, Join<StudentTask, Request>> user = studentTask.join("user");
            List<Predicate> groupIdPredicate = new ArrayList<>();
            for (Long groupId : groupIds) {
                groupIdPredicate.add(criteriaBuilder.equal(user.get("group"), groupId));
            }
            return criteriaBuilder.or(groupIdPredicate.toArray(new Predicate[0]));
        };
    }

    private Specification<Request> hasTeacher(Long teacherId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.<Request>get("teacher"), teacherId);
    }

    private Specification<Request> hasRequestTypes(List<Long> requestTypeIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> requestTypePredicates = new ArrayList<>();
            Path<RequestType> requestType = root.get("requestType");
            for (Long requestTypeId : requestTypeIds) {
                requestTypePredicates.add(criteriaBuilder.equal(requestType, requestTypeId));
            }
            return criteriaBuilder.or(requestTypePredicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Request> hasRequestStates(List<Long> requestStateIds) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> requestStatePredicates = new ArrayList<>();
            Path<RequestState> requestState = root.get("requestState");
            for (Long requestStateId : requestStateIds) {
                requestStatePredicates.add(criteriaBuilder.equal(requestState, requestStateId));
            }
            return criteriaBuilder.or(requestStatePredicates.toArray(new Predicate[0]));
        };
    }

}
