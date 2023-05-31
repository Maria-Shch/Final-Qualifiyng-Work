package ru.shcherbatykh.Backend.security;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
