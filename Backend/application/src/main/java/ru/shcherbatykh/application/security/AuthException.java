package ru.shcherbatykh.application.security;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
