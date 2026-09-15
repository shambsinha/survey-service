package com.survey.survey_service.validator;

import com.survey.survey_service.enums.DataType;

public interface FieldValidator {
    DataType getSupportedType();
    boolean validate(String value, String optionsJson);
}
