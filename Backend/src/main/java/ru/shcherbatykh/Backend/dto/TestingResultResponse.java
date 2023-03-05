package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.classes.AppError;
import ru.shcherbatykh.Backend.models.Status;

@Getter
@Setter
public class TestingResultResponse {

    private Status status;
    private boolean testingSuccessfulCompleted;
    private AppError testError;

    public TestingResultResponse(Status status, boolean isTestingSuccessfulCompleted) {
        this.status = status;
        this.testingSuccessfulCompleted = isTestingSuccessfulCompleted;
    }

    public TestingResultResponse(Status status, boolean isTestingSuccessfulCompleted, AppError testError) {
        this.status = status;
        this.testingSuccessfulCompleted = isTestingSuccessfulCompleted;
        this.testError = testError;
    }
}
