package com.survey.survey_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldRequest {
    private String name;
    private String label;
    private Long dataTypeId;
    private Boolean isRequired;
    private Integer displayOrder;
    private String optionsJson;
}
