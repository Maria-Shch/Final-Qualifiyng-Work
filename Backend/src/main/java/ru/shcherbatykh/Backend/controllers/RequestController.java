package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.Filter;
import ru.shcherbatykh.Backend.models.*;
import ru.shcherbatykh.Backend.services.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/request")
public class RequestController {
    private final RequestService requestService;
    private final AuthService authService;
    private final RequestStateService requestStateService;
    private final RequestTypeService requestTypeService;
    private final TaskService taskService;
    private final StudentTaskService studentTaskService;

    public RequestController(RequestService requestService, AuthService authService,
                             RequestStateService requestStateService, RequestTypeService requestTypeService,
                             TaskService taskService, StudentTaskService studentTaskService) {
        this.requestService = requestService;
        this.authService = authService;
        this.requestStateService = requestStateService;
        this.requestTypeService = requestTypeService;
        this.taskService = taskService;
        this.studentTaskService = studentTaskService;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/count")
    public long getCountOfRequestsByTeacher(){
        User teacher = authService.getUser().orElse(null);
        return requestService.getCountOfRequestsByTeacher(teacher);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/types")
    public List<RequestType> getRequestTypes(){
        return requestTypeService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/states")
    public List<RequestState> getRequestStates(){
        return requestStateService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping("/count/filter")
    public int getCountRequestsAfterFiltering(@RequestBody Filter filter) {
        User teacher = authService.getUser().orElse(null);
        return requestService.getCountRequestsAfterFiltering(teacher, filter);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping("/get/{pageNumber}")
    public List<Request> getRequestsByPageNumberAndFilter(@PathVariable int pageNumber,
                                                          @RequestBody(required = false) Filter filter) {
        User teacher = authService.getUser().orElse(null);
        List<Request> requests;
        if (filter == null){
            requests = requestService.getRequestsByTeacherAndPageNumber(teacher, pageNumber);
        } else {
            requests = requestService.getRequestsByPageNumberAndFilter(teacher, pageNumber, filter);
        }
        return requestService.setToNullSomeFields(requests);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/{id}")
    public Request getRequestById(@PathVariable long id) {
        User teacher = authService.getUser().orElse(null);
        Request request = requestService.findById(id);
        if(request != null && request.getTeacher().getId() == teacher.getId()){
            if (Objects.equals(request.getRequestState().getName(), "Не просмотрен")){
                return requestService.markRequestViewed(request);
            } else return request;
        } else return null;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/getClasses/{idStudentTask}")
    public List<String> getCodes(@PathVariable long idStudentTask) {
        return taskService.getClassesForTask(studentTaskService.findById(idStudentTask));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping("/reject/{requestId}")
    public Request rejectSolution(@PathVariable int requestId, @RequestBody(required = false) String teacherMsg) {
        return requestService.rejectSolution(requestId, teacherMsg);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping("/accept/{requestId}")
    public Request acceptSolution(@PathVariable int requestId, @RequestBody(required = false) String teacherMsg) {
        return requestService.acceptSolution(requestId, teacherMsg);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/getHistory/{pageNumber}")
    public List<EventHistory> getHistoryOfRequests(@PathVariable int pageNumber) {
        User user = authService.getUser().orElse(null);
        return requestService.getHistoryOfRequests(user, pageNumber);
    }
}
