package ru.shcherbatykh.codetester.error_handling;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@Getter
public enum ErrorCode {
    ACH_000("ACH-000", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    ACH_001("ACH-001", "Request body must contain at least one code source", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    ErrorCode(String errorCode, String errorMessage, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public String getErrorMessage(Object... params) {
        return MessageFormat.format(errorMessage, params);
    }
}
