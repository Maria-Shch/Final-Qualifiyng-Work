package ru.shcherbatykh.application.dto;

import lombok.Getter;
import lombok.Setter;
import ru.shcherbatykh.application.classes.AppError;
import ru.shcherbatykh.application.models.Status;

@Getter
@Setter
public class SendingOnTestingResponse {
    private boolean codeSuccessfulSent;
    private Status status;
    private AppError sendingError;

    public SendingOnTestingResponse(boolean codeSuccessfulSent, Status status) {
        this.codeSuccessfulSent = codeSuccessfulSent;
        this.status = status;
    }

    public SendingOnTestingResponse(boolean codeSuccessfulSent, Status status, AppError sendingError) {
        this.codeSuccessfulSent = codeSuccessfulSent;
        this.status = status;
        this.sendingError = sendingError;
    }

    public SendingOnTestingResponse(boolean codeSuccessfulSent, AppError sendingError) {
        this.codeSuccessfulSent = codeSuccessfulSent;
        this.sendingError = sendingError;
    }

    public SendingOnTestingResponse(boolean codeSuccessfulSent) {
        this.codeSuccessfulSent = codeSuccessfulSent;
    }
}
