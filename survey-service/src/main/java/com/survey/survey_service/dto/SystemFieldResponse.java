package com.survey.survey_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemFieldResponse {
    private Long id;
    private String name;
    private String label;
    private String dataType;
    private Boolean isDefault;
    private String optionsJson;
}
