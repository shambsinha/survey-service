package com.survey.survey_service.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormResponse {
    private Long id;
    private String title;
    private String description;
    private List<FieldResponse> fields;
}
