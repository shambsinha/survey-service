package com.survey.survey_service.dto;

import com.survey.survey_service.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleAssignRequest {
    private Role role;
}
