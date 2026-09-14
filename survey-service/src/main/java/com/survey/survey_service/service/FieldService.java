package com.survey.survey_service.service;

import com.survey.survey_service.dto.FieldRequest;
import com.survey.survey_service.dto.FieldResponse;
import com.survey.survey_service.entity.Field;

import java.util.List;

public interface FieldService {
    Field createSystemField(FieldRequest request, Long userId);

    List<FieldResponse> getSystemFields();
}
