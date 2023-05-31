package ru.shcherbatykh.common.model;

import lombok.Getter;

@Getter
public enum ResponseCode {
    CH_000("CH-000", "Все тесты успешно пройдены"),
    CH_001("CH-001", "Не все тесты успешно пройдены"),
    CH_002("CH-002", "Техническая ошибка сервера"),
    CH_003("CH-003", "Ошибка при разборе кода"),
    CH_004("CH-004", "Ошибка компиляции");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
