package ru.shcherbatykh.Backend.security;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AuthResponseStatus {
    SUCCESS("SUCCESS"), ERROR("ERROS");
    private final String value;
}
