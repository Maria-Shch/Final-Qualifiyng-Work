package ru.shcherbatykh.codetester.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.codetester.services.TestClassProvider;

@RestController
public class CodetestController {
    private final TestClassProvider testClassProvider;

    public CodetestController(TestClassProvider testClassProvider) {
        this.testClassProvider = testClassProvider;
    }

    @GetMapping("/get/testClass/{taskId}")
    public String getActualTestClass(@PathVariable long taskId) {
        return testClassProvider.getActualTestClass(taskId);
    }
}
