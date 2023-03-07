package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.Backend.models.Request;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.RequestService;

import java.util.List;

@RestController
@RequestMapping("/request")
public class RequestController {
    private final RequestService requestService;
    private final AuthService authService;

    public RequestController(RequestService requestService, AuthService authService) {
        this.requestService = requestService;
        this.authService = authService;
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/count")
    public long getCountOfRequestsByTeacher(){
        User teacher = authService.getUser().orElse(null);
        return requestService.getCountOfRequestsByTeacher(teacher);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/get/{pageNumber}")
    public List<Request> getRequestsByPageNumber(@PathVariable int pageNumber){
        User teacher = authService.getUser().orElse(null);
        List<Request> requestsByTeacherAndPageNumber = requestService.getRequestsByTeacherAndPageNumber(teacher,
                pageNumber - 1);
        return requestService.setToNullSomeFields(requestsByTeacherAndPageNumber);
    }

}
