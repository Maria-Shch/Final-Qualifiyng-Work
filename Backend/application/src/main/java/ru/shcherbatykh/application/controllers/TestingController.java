package ru.shcherbatykh.application.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.shcherbatykh.application.classes.CodeCheckResponseResult;
import ru.shcherbatykh.application.classes.TestDefinitionResponseResult;
import ru.shcherbatykh.application.dto.ResponseAboutTestingAllowed;
import ru.shcherbatykh.application.dto.SendingOnTestingResponse;
import ru.shcherbatykh.application.models.User;
import ru.shcherbatykh.application.services.AuthService;
import ru.shcherbatykh.application.services.TestingService;

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
        return testingService.getResponseAboutTestingAllowed(
                serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask, authService.getUser().orElse(null));
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @PostMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/onTestingS")
    public SendingOnTestingResponse onTestingForStudent(@PathVariable int serialNumberOfChapter, @PathVariable int serialNumberOfBlock,
                                                        @PathVariable int serialNumberOfTask, @RequestBody List<String> codes) {
        return testingService.testingForStudent(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null), codes);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/chapter/{serialNumberOfChapter}/block/{serialNumberOfBlock}/task/{serialNumberOfTask}/getTestingResultS")
    public CodeCheckResponseResult getTestingResultForStudent(@PathVariable int serialNumberOfChapter,
                                                              @PathVariable int serialNumberOfBlock,
                                                              @PathVariable int serialNumberOfTask){
        return testingService.getTestingResultForStudent(serialNumberOfChapter, serialNumberOfBlock, serialNumberOfTask,
                authService.getUser().orElse(null));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping("/{studentTaskId}/onTestingT")
    public SendingOnTestingResponse onTestingForTeacher(@PathVariable long studentTaskId, @RequestBody List<String> codes) {
        User teacher = authService.getUser().orElse(null);
        return testingService.testingForTeacher(studentTaskId, teacher, codes);
    }

    @PreAuthorize("hasAnyAuthority('USER','TEACHER','ADMIN')")
    @GetMapping("/auth/{studentTaskId}/getTestingResultT")
    public CodeCheckResponseResult getTestingResultForTeacher(@PathVariable long studentTaskId){
        return testingService.getTestingResultForTeacher(studentTaskId, authService.getUser().orElse(null));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/saveCodeTest/{taskId}")
    public boolean sendCodeTest(@PathVariable long taskId, @RequestBody String codeTest) {
        return testingService.sendCodeTest(taskId, codeTest);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/saveCodeTest/{taskId}/result")
    public TestDefinitionResponseResult getTestDefinitionResponseResult(@PathVariable long taskId){
        return testingService.getTestDefinitionResponseResult(taskId);
    }
}