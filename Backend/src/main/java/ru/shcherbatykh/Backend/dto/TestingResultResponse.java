package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.classes.TestError;
import ru.shcherbatykh.Backend.models.Status;

@Getter
@Setter
public class TestingResultResponse {

    private Status status;
    private boolean testingSuccessfulCompleted;
    private TestError testError;

    public TestingResultResponse(Status status, boolean isTestingSuccessfulCompleted) {
        this.status = status;
        this.testingSuccessfulCompleted = isTestingSuccessfulCompleted;
    }

    public TestingResultResponse(Status status, boolean isTestingSuccessfulCompleted, TestError testError) {
        this.status = status;
        this.testingSuccessfulCompleted = isTestingSuccessfulCompleted;
        this.testError = testError;
    }
}
