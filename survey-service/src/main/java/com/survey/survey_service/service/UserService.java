package com.survey.survey_service.service;

import com.survey.survey_service.dto.RoleAssignRequest;

public interface UserService {
    void assignRole(Long userId, RoleAssignRequest request);
}
