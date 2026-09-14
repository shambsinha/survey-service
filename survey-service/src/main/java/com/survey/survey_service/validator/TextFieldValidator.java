package com.survey.survey_service.validator;

import com.survey.survey_service.enums.DataType;
import org.springframework.stereotype.Component;

@Component
public class TextFieldValidator implements FieldValidator {
    @Override
    public boolean supports(DataType dataType) {
        return dataType == DataType.TEXT;
    }

    @Override
    public boolean validate(String value, String optionsJson) {
        return true;
    }
}
