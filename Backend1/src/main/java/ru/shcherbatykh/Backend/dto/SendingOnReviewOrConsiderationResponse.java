package ru.shcherbatykh.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.Backend.classes.AppError;
import ru.shcherbatykh.Backend.models.Status;

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
