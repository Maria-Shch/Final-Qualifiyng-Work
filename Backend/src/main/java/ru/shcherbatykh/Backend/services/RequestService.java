package ru.shcherbatykh.Backend.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.shcherbatykh.Backend.dto.FilterRequests;
import ru.shcherbatykh.Backend.models.*;
import ru.shcherbatykh.Backend.repositories.RequestRepo;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RequestService {
    private final RequestRepo requestRepo;
    private final UserService userService;
    private final RequestTypeService requestTypeService;
    private final RequestStateService requestStateService;
    private final EventHistoryService eventHistoryService;
    private final ClosingStatusService closingStatusService;
    private final StudentTaskService studentTaskService;

    public RequestService(RequestRepo requestRepo, UserService userService, RequestTypeService requestTypeService,
                          RequestStateService requestStateService, EventHistoryService eventHistoryService,
                          ClosingStatusService closingStatusService, StudentTaskService studentTaskService) {
        this.requestRepo = requestRepo;
        this.userService = userService;
        this.requestTypeService = requestTypeService;
        this.requestStateService = requestStateService;
        this.eventHistoryService = eventHistoryService;
        this.closingStatusService = closingStatusService;
        this.studentTaskService = studentTaskService;
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
            setToNullSomeFields(request);
        }
        return requests;
    }

    public Request setToNullSomeFields(Request request){
        request.getStudentTask().getTask().setDescription(null);
        request.getStudentTask().getTask().getBlock().setTextTheory(null);
        request.getStudentTask().getUser().setPassword(null);
        request.getTeacher().setPassword(null);
        if ( request.getStudentTask().getUser().getGroup() != null){
            request.getStudentTask().getUser().getGroup().setTeacher(null);
        }
        return request;
    }


    public int getCountRequestsAfterFiltering(User teacher, FilterRequests filterRequests) {
        return requestRepo.count(getSpecification(teacher, filterRequests));
    }

    public List<Request> getRequestsByPageNumberAndFilter(User teacher, int pageNumber, FilterRequests filterRequests) {
        Sort sort;
        if (filterRequests.isAscending()){
           sort = Sort.by("creationTime").ascending();
        } else {
            sort = Sort.by("creationTime").descending();
        }
        Pageable sortedByCreationTimeDesc =
                PageRequest.of(pageNumber, 10, sort);
        return requestRepo.findAll(getSpecification(teacher, filterRequests), sortedByCreationTimeDesc);
    }

    private Specification<Request> getSpecification(User teacher, FilterRequests filterRequests) {

        Specification<Request> specification = hasTeacher(teacher.getId());

        if (!CollectionUtils.isEmpty(filterRequests.getRequestStateIds())) {
            specification = specification.and(hasRequestStates(filterRequests.getRequestStateIds()));
        }

        if (!CollectionUtils.isEmpty(filterRequests.getRequestTypeIds())) {
            specification = specification.and(hasRequestTypes(filterRequests.getRequestTypeIds()));
        }

        if (!CollectionUtils.isEmpty(filterRequests.getGroupIds())) {
            specification = specification.and(hasGroupIds(filterRequests.getGroupIds()));
        }

        return specification;
    }

    private Specification<Request> hasGroupIds(List<Long> groupIds) {
        return (root, query, criteriaBuilder) -> {
            Join<StudentTask, Request> studentTask = root.join("studentTask");
            Join<User, Join<StudentTask, Request>> user = studentTask.join("user");
            List<Predicate> groupIdPredicate = new ArrayList<>();
            for (Long groupId : groupIds) {
               if (groupId != null){
                   groupIdPredicate.add(criteriaBuilder.equal(user.get("group"), groupId));
               } else {
                   groupIdPredicate.add(criteriaBuilder.isNull(user.get("group")));
               }
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

    public Request findById(long id) {
        return requestRepo.findById(id).orElse(null);
    }

    public Request markRequestViewed(Request request){
        request.setRequestState(requestStateService.getRSSeen());
        return requestRepo.save(request);
    }

    public Request rejectSolution(int requestId, String teacherMsg) {
        Request request = closingRequest(requestId, teacherMsg);
        request.setClosingStatus(closingStatusService.getCSSolutionRejected());
        eventHistoryService.registerEventTeacherRejected(request);
        studentTaskService.setStatusRejected(request.getStudentTask());
        return setToNullSomeFields(requestRepo.save(request));
    }

    public Request acceptSolution(int requestId, String teacherMsg) {
        Request request = closingRequest(requestId, teacherMsg);
        request.setClosingStatus(closingStatusService.getCSSolutionAccepted());
        eventHistoryService.registerEventTeacherAccepted(request);
        studentTaskService.setStatusSolved(request.getStudentTask());
        return setToNullSomeFields(requestRepo.save(request));
    }

    private Request closingRequest(int requestId, String teacherMsg){
        Request request = findById(requestId);
        request.setRequestState(requestStateService.getRSProcessed());
        request.setTeacherMsg(teacherMsg);
        request.setClosingTime(LocalDateTime.now());
        return request;
    }

    public List<EventHistory> getHistoryOfRequests(User user, int pageNumber) {
        List<EventHistory> histories = eventHistoryService.getHistoryByUserAndPageNumber(user, pageNumber);
        for(EventHistory history: histories){
            Request newRequest = setToNullSomeFields(history.getRequest());
            history.setRequest(newRequest);
        }
        return histories;
    }

    public void revokeRequestsFromTeacher(long teacherId) {
        User teacher = userService.findById(teacherId).get();
        List<RequestState> incompleteRequestStates = List.of(requestStateService.getRSNotSeen(), requestStateService.getRSSeen());
        List<Request> incompleteRequests = requestRepo.findAllByTeacherAndAndRequestStateIn(teacher, incompleteRequestStates);
        for(Request request: incompleteRequests){
            request.setTeacher(userService.getAdmin());
            if (Objects.equals(request.getRequestState().getId(), requestStateService.getRSSeen().getId())){
                request.setRequestState(requestStateService.getRSNotSeen());
            }
            requestRepo.save(request);
        }
    }
}
