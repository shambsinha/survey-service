package com.survey.survey_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormUpdateRequest {
    private String title;
    private String description;
}
