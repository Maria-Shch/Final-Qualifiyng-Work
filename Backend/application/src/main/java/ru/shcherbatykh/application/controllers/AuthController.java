package ru.shcherbatykh.application.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shcherbatykh.application.security.AuthResponse;
import ru.shcherbatykh.application.security.JwtRequest;
import ru.shcherbatykh.application.security.RefreshJwtRequest;
import ru.shcherbatykh.application.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody JwtRequest authRequest) {
        final AuthResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final AuthResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        final AuthResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
