package com.survey.survey_service.validator;

import com.survey.survey_service.enums.DataType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FieldValidatorMap {
    private final Map<DataType, FieldValidator> validatorMap = new EnumMap<>(DataType.class);

    public FieldValidatorMap(List<FieldValidator> validators) {
        for (FieldValidator validator : validators) {
            validatorMap.put(validator.getSupportedType(), validator);
        }
    }

    public FieldValidator getValidator(DataType dataType) {
        return validatorMap.get(dataType);
    }
}

