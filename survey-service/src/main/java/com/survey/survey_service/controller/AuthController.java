package com.survey.survey_service.controller;

import com.survey.survey_service.dto.LoginRequest;
import com.survey.survey_service.dto.LoginResponse;
import com.survey.survey_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody com.survey.survey_service.dto.RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered!!!!!!!");
    }
}
