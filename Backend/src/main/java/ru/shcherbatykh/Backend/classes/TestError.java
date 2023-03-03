package ru.shcherbatykh.Backend.classes;

import lombok.Getter;

@Getter
public enum TestError {
    TEST_ERR_001("TEST_ERR_001", "Не удалось сохранить код");

    private final String code;
    private final String message;

    TestError(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
