package com.survey.survey_service.service;

import com.survey.survey_service.dto.LoginRequest;
import com.survey.survey_service.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
