package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.Filter;
import ru.shcherbatykh.Backend.models.*;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.RequestService;
import ru.shcherbatykh.Backend.services.RequestStateService;
import ru.shcherbatykh.Backend.services.RequestTypeService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {
    private final RequestService requestService;
    private final AuthService authService;
    private final RequestStateService requestStateService;
    private final RequestTypeService requestTypeService;

    public RequestController(RequestService requestService, AuthService authService,
                             RequestStateService requestStateService, RequestTypeService requestTypeService) {
        this.requestService = requestService;
        this.authService = authService;
        this.requestStateService = requestStateService;
        this.requestTypeService = requestTypeService;
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
}
