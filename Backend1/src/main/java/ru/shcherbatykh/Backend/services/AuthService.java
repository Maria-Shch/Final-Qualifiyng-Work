package ru.shcherbatykh.Backend.services;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.shcherbatykh.Backend.models.User;
import ru.shcherbatykh.Backend.security.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.error.message.unknown-username}")
    private String errMsgUnknownUsername;

    @Value("${app.error.message.incorrect-password}")
    private String errMsgIncorrectPassword;

    private final Map<String, String> refreshStorage = new HashMap<>();

    public AuthResponse login(@NonNull JwtRequest authRequest) {
        final User user = userService.findByUsername(authRequest.getUsername()).orElse(null);
        if(user == null) return new AuthResponse(errMsgUnknownUsername);

        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getUsername(), refreshToken);
            return new AuthResponse(accessToken, refreshToken, user);
        } else {
            return new AuthResponse(errMsgIncorrectPassword);
        }
    }

    public AuthResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.findByUsername(login).orElse(null);
                if(user == null) return new AuthResponse(errMsgUnknownUsername);
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new AuthResponse(accessToken, null, user);
            }
        }
        return new AuthResponse(null, null, null);
    }

    public AuthResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.findByUsername(login).orElse(null);
                if(user == null) return new AuthResponse(errMsgUnknownUsername);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getUsername(), newRefreshToken);
                return new AuthResponse(accessToken, newRefreshToken, user);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public Optional<User> getUser() {
        JwtAuthentication jwtAuthentication =  (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(jwtAuthentication.getUsername());
    }
}
