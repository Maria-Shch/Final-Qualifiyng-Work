package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.classes.AppError;
import ru.shcherbatykh.application.models.Status;

@Getter
@Setter
public class SendingOnReviewOrConsiderationResponse {
    private Status status;
    private boolean sendingSuccessfulCompleted;
    private AppError testError;

    public SendingOnReviewOrConsiderationResponse(Status status, boolean sendingSuccessfulCompleted) {
        this.status = status;
        this.sendingSuccessfulCompleted = sendingSuccessfulCompleted;
    }

    public SendingOnReviewOrConsiderationResponse(Status status, boolean sendingSuccessfulCompleted, AppError testError) {
        this.status = status;
        this.sendingSuccessfulCompleted = sendingSuccessfulCompleted;
        this.testError = testError;
    }
}
