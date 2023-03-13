package ru.shcherbatykh.Backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.Backend.dto.ResponseAboutTestingAllowed;
import ru.shcherbatykh.Backend.dto.TestingResultResponse;
import ru.shcherbatykh.Backend.services.AuthService;
import ru.shcherbatykh.Backend.services.TestingService;

import java.util.List;

@RestController
public class TestingController {

    private final AuthService authService;
    private final TestingService testingService;

    public TestingController(AuthService authService, TestingService testingService) {
        this.authService = authService;
        this.testingService = testingService;
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/isTestingAllowed")
    public ResponseAboutTestingAllowed isTestingAllowed(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                                        @PathVariable int serialNumberOfTask) {
        ResponseAboutTestingAllowed responseAboutTestingAllowed = testingService.getResponseAboutTestingAllowed(
                serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask, authService.getUser().orElse(null));
        return responseAboutTestingAllowed;
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/onTestingS")
    public TestingResultResponse onTestingForStudent(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                                     @PathVariable int serialNumberOfTask, @RequestBody List<String> codes) {
        return testingService.testingForStudent(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null), codes);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping("/{studentTaskId}/onTestingT")
    public TestingResultResponse onTestingForTeacher(@PathVariable long studentTaskId, @RequestBody List<String> codes) {
        return testingService.testingForTeacher(studentTaskId, codes);
    }
}