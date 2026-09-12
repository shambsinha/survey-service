package com.survey.survey_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDetail {
    private Long submittedBy;
    private String value;
    private Long userId;
}
