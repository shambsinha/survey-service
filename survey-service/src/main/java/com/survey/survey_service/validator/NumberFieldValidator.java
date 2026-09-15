package com.survey.survey_service.validator;

import com.survey.survey_service.enums.DataType;
import org.springframework.stereotype.Component;

@Component
public class NumberFieldValidator implements FieldValidator {
    @Override
    public DataType getSupportedType() {
        return DataType.NUMBER;
    }

    @Override
    public boolean validate(String value, String optionsJson) {
        if (value == null || value.trim().isEmpty()) return true;
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
