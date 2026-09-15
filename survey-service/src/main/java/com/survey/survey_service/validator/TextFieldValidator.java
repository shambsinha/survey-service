package com.survey.survey_service.validator;

import com.survey.survey_service.enums.DataType;
import org.springframework.stereotype.Component;

@Component
public class TextFieldValidator implements FieldValidator {
    @Override
    public DataType getSupportedType() {
        return DataType.TEXT;
    }

    @Override
    public boolean validate(String value, String optionsJson) {
        return true;
    }
}
