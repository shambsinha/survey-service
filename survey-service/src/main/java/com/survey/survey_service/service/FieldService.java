package com.survey.survey_service.service;

import com.survey.survey_service.dto.FieldRequest;
import com.survey.survey_service.entity.Field;

public interface FieldService {
    Field createSystemField(FieldRequest request, Long userId);
}
