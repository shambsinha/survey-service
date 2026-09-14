package com.survey.survey_service.service.impl;

import com.survey.survey_service.dto.FieldRequest;
import com.survey.survey_service.entity.Field;
import com.survey.survey_service.enums.DataType;
import com.survey.survey_service.repository.FieldRepository;
import com.survey.survey_service.service.FieldService;
import org.springframework.stereotype.Service;

@Service
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;

    public FieldServiceImpl(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    public Field createSystemField(FieldRequest request, Long userId) {
        Field field = new Field();
        field.setDataType(DataType.valueOf(request.getDataType().toUpperCase()));
        field.setName(request.getName());
        field.setLabel(request.getLabel());
        field.setOptionsJson(request.getOptionsJson());
        field.setIsSystem(true);
        field.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        field.setCreatedBy(userId);
        field.setUpdatedBy(userId);
        
        return fieldRepository.save(field);
    }
}
