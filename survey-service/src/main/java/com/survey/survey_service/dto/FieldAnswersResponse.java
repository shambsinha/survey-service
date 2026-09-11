package com.survey.survey_service.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldAnswersResponse {
    private FieldResponse field;
    private List<AnswerDetail> answers;
}
