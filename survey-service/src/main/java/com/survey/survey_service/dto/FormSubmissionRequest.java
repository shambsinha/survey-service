package com.survey.survey_service.dto;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormSubmissionRequest {
    private Map<Long, String> values;
}
