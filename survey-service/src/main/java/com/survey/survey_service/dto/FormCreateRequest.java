package com.survey.survey_service.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormCreateRequest {
    private Long userId;
    private String title;
    private String description;
    private List<FieldRequest> fields;
}
