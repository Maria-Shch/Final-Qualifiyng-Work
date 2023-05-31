package ru.shcherbatykh.application.security;

import lombok.Getter;
import ru.shcherbatykh.application.models.User;

@Getter
public class AuthResponse{
    private final AuthResponseStatus authResponseStatus;
    private String errorMessage;
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
    private User user;

    public AuthResponse(String accessToken, String refreshToken, User user) {
        this.authResponseStatus = AuthResponseStatus.SUCCESS;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public AuthResponse(String errorMessage) {
        this.authResponseStatus = AuthResponseStatus.ERROR;
        this.errorMessage = errorMessage;
    }
}
