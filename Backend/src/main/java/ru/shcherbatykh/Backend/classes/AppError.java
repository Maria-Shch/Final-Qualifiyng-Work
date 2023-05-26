package ru.shcherbatykh.Backend.classes;

import lombok.Getter;

@Getter
public enum AppError {
    APP_ERR_001("APP_ERR_001", "Не удалось сохранить код");

    private final String code;
    private final String message;

    AppError(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
