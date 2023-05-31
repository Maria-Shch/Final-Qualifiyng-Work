package ru.shcherbatykh.application.security;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthResponseStatus {
    SUCCESS("SUCCESS"), ERROR("ERROS");
    private final String value;
}
