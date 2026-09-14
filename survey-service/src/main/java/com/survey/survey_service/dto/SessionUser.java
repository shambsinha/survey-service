package com.survey.survey_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionUser {
    private Long id;
    private String username;
    private String role;
}
