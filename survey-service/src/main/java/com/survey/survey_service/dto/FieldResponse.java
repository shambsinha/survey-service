package com.survey.survey_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldResponse {
    private Long fieldId;
    private String name;
    private String label;
    private String dataType;
    private Boolean isRequired;
    private Integer displayOrder;
    private String optionsJson;
}
