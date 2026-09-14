package com.survey.survey_service.service;

import com.survey.survey_service.dto.LoginRequest;
import com.survey.survey_service.dto.LoginResponse;
import com.survey.survey_service.dto.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void register(RegisterRequest request);
}
