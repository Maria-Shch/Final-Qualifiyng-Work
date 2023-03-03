package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.classes.TestError;

@Getter
@Setter
public class TestingResultResponse {
    private boolean isTestingSuccessfulCompleted;
    private TestError testError;

    public TestingResultResponse(boolean isTestingSuccessfulCompleted) {
        this.isTestingSuccessfulCompleted = isTestingSuccessfulCompleted;
    }

    public TestingResultResponse(boolean isTestingSuccessfulCompleted, TestError testError) {
        this.isTestingSuccessfulCompleted = isTestingSuccessfulCompleted;
        this.testError = testError;
    }
}
