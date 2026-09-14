package com.survey.survey_service.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.survey_service.enums.DataType;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DropdownFieldValidator implements FieldValidator {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(DataType dataType) {
        return dataType == DataType.DROPDOWN;
    }

    @Override
    public boolean validate(String value, String optionsJson) {
        if (value == null || value.trim().isEmpty()) return true;
        if (optionsJson == null || optionsJson.trim().isEmpty()) return true;
        try {
            List<String> opts = mapper.readValue(optionsJson, new TypeReference<List<String>>(){});
            return opts.contains(value);
        } catch (Exception ex) {
            return false;
        }
    }
}
