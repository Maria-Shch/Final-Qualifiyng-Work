package ru.shcherbatykh.Backend.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.shcherbatykh.Backend.models.User;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
    private User user;
}
