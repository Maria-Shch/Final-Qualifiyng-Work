package ru.shcherbatykh.common.model;

import lombok.Getter;

@Getter
public enum TestDefinitionResponseCode {
    TD_000("TD-000", "Код теста успешно скомпилирован и загружен"),
    TD_001("TD-001", "Техническая ошибка сервера"),
    TD_002("TD-002", "Ошибки при валидации кода теста"),
    TD_003("TD-003", "Ошибки при компиляции теста");

    private final String code;
    private final String message;

    TestDefinitionResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
