package ru.shcherbatykh.autochecker.error_handling;

import lombok.Getter;

@Getter
public class RequestValidationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] errorParams;

    public RequestValidationException(ErrorCode errorCode, Object... errorParams) {
        super(errorCode.getErrorMessage(errorParams));
        this.errorCode = errorCode;
        this.errorParams = errorParams;
    }
}
