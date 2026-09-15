package com.survey.survey_service.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.survey_service.enums.DataType;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MultiselectFieldValidator implements FieldValidator {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public DataType getSupportedType() {
        return DataType.MULTISELECT;
    }

    @Override
    public boolean validate(String value, String optionsJson) {
        if (value == null || value.trim().isEmpty()) return true;
        if (optionsJson == null || optionsJson.trim().isEmpty()) return true;
        try {
            List<String> opts = mapper.readValue(optionsJson, new TypeReference<List<String>>(){});
            List<String> userVals = mapper.readValue(value, new TypeReference<List<String>>(){});
            for (String v : userVals) {
                if (!opts.contains(v)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
