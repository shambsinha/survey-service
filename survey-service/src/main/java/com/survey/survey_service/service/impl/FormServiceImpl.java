package com.survey.survey_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.survey_service.dto.*;
import com.survey.survey_service.entity.*;
import com.survey.survey_service.repository.*;
import com.survey.survey_service.service.FormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;
    private final FieldRepository fieldRepository;
    private final FormFieldMappingRepository formFieldMappingRepository;
    private final FormValueRepository formValueRepository;
    private final DataTypeRepository dataTypeRepository;

    public FormServiceImpl(FormRepository formRepository, 
                           FieldRepository fieldRepository, 
                           FormFieldMappingRepository formFieldMappingRepository,
                           FormValueRepository formValueRepository,
                           DataTypeRepository dataTypeRepository) {
        this.formRepository = formRepository;
        this.fieldRepository = fieldRepository;
        this.formFieldMappingRepository = formFieldMappingRepository;
        this.formValueRepository = formValueRepository;
        this.dataTypeRepository = dataTypeRepository;
    }

    @Override
    @Transactional
    public FormResponse createForm(FormCreateRequest request) {
        Form form = new Form();
        form.setUserId(request.getUserId());
        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());
        form = formRepository.save(form);

        List<FieldResponse> fieldResponses = new ArrayList<>();
        int currentDisplayOrder = 1;

        List<Field> defaultFields = fieldRepository.findByIsDefaultTrue();
        for (Field defaultField : defaultFields) {
            FormFieldMapping mapping = new FormFieldMapping();
            mapping.setFormId(form.getId());
            mapping.setFieldId(defaultField.getId());
            mapping.setIsRequired(true);
            mapping.setDisplayOrder(currentDisplayOrder++);
            formFieldMappingRepository.save(mapping);

            FieldResponse fr = new FieldResponse();
            fr.setFieldId(defaultField.getId());
            fr.setName(defaultField.getName());
            fr.setLabel(defaultField.getLabel());
            fr.setDataTypeId(defaultField.getDataTypeId());
            fr.setOptionsJson(defaultField.getOptionsJson());
            fr.setIsRequired(true);
            fr.setDisplayOrder(mapping.getDisplayOrder());
            fieldResponses.add(fr);
        }
        if (request.getFields() != null) {
            for (FieldRequest fr : request.getFields()) {
                if (!dataTypeRepository.existsById(fr.getDataTypeId())) {
                    throw new RuntimeException("invalid data type id");
                }
                
                Field field = new Field();
                field.setUserId(request.getUserId());
                field.setDataTypeId(fr.getDataTypeId());
                field.setName(fr.getName());
                field.setLabel(fr.getLabel());
                field.setOptionsJson(fr.getOptionsJson());
                field.setIsDefault(false);
                field = fieldRepository.save(field);

                FormFieldMapping mapping = new FormFieldMapping();
                mapping.setFormId(form.getId());
                mapping.setFieldId(field.getId());
                mapping.setIsRequired(fr.getIsRequired());

                int order = (fr.getDisplayOrder() != null) ? fr.getDisplayOrder() : currentDisplayOrder++;
                mapping.setDisplayOrder(order);
                formFieldMappingRepository.save(mapping);

                FieldResponse fieldResponse = new FieldResponse();
                fieldResponse.setFieldId(field.getId());
                fieldResponse.setName(field.getName());
                fieldResponse.setLabel(field.getLabel());
                fieldResponse.setDataTypeId(field.getDataTypeId());
                fieldResponse.setOptionsJson(field.getOptionsJson());
                fieldResponse.setIsRequired(mapping.getIsRequired());
                fieldResponse.setDisplayOrder(mapping.getDisplayOrder());
                fieldResponses.add(fieldResponse);
            }
        }

        FormResponse response = new FormResponse();
        response.setId(form.getId());
        response.setTitle(form.getTitle());
        response.setDescription(form.getDescription());
        response.setFields(fieldResponses);

        return response;
    }

    @Override
    public FormResponse getForm(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        List<FormFieldMapping> mappings = formFieldMappingRepository.findByFormId(formId);
        List<FieldResponse> fieldResponses = new ArrayList<>();

        for (FormFieldMapping mapping : mappings) {
            fieldRepository.findById(mapping.getFieldId()).ifPresent(field -> {
                FieldResponse fr = new FieldResponse();
                fr.setFieldId(field.getId());
                fr.setName(field.getName());
                fr.setLabel(field.getLabel());
                fr.setDataTypeId(field.getDataTypeId());
                fr.setOptionsJson(field.getOptionsJson());
                fr.setIsRequired(mapping.getIsRequired());
                fr.setDisplayOrder(mapping.getDisplayOrder());
                fieldResponses.add(fr);
            });
        }

        FormResponse response = new FormResponse();
        response.setId(form.getId());
        response.setTitle(form.getTitle());
        response.setDescription(form.getDescription());
        response.setFields(fieldResponses);

        return response;
    }

    @Override
    @Transactional
    public void submitForm(Long formId, FormSubmissionRequest request, Long uId) {

        if (request.getValues() != null) {
            for (Map.Entry<Long, String> entry : request.getValues().entrySet()) {
                Long fieldId = entry.getKey();
                String submittedValue = entry.getValue();

                Field field = fieldRepository.findById(fieldId)
                        .orElseThrow(() -> new RuntimeException("field not found"));

                DataType dataType = dataTypeRepository.findById(field.getDataTypeId())
                        .orElseThrow(() -> new RuntimeException("datatype not found"));

                boolean isValid = this.validateField(dataType.getName(), submittedValue, field.getOptionsJson());
                if (!isValid) {
                    throw new RuntimeException("invalid data type");
                }

                FormValue formValue = new FormValue();
                formValue.setSubmittedBy(uId);
                formValue.setFormId(formId);
                formValue.setFieldId(fieldId);
                formValue.setValue(submittedValue);
                formValueRepository.save(formValue);
            }
        }
    }

    @Override
    public List<FieldAnswersResponse> getFormResults(Long formId) {
        List<FormValue> allValues = formValueRepository.findByFormId(formId);
        List<FormFieldMapping> mappings = formFieldMappingRepository.findByFormId(formId);
        Map<Long, List<FormValue>> valuesByFieldId = allValues.stream()
                .collect(Collectors.groupingBy(FormValue::getFieldId));

        List<FieldAnswersResponse> resultList = new ArrayList<>();

        for (FormFieldMapping mapping : mappings) {
            Field field = fieldRepository.findById(mapping.getFieldId()).orElse(null);
            if (field != null) {
                FieldResponse fieldDto = new FieldResponse();
                fieldDto.setFieldId(field.getId());
                fieldDto.setName(field.getName());
                fieldDto.setLabel(field.getLabel());
                fieldDto.setDataTypeId(field.getDataTypeId());
                fieldDto.setOptionsJson(field.getOptionsJson());
                fieldDto.setIsRequired(mapping.getIsRequired());
                fieldDto.setDisplayOrder(mapping.getDisplayOrder());

                List<AnswerDetail> answers = new ArrayList<>();


                List<FormValue> fieldValues = valuesByFieldId.getOrDefault(field.getId(), new ArrayList<>());

                for (FormValue value : fieldValues) {
                    AnswerDetail ans = new AnswerDetail();
                    ans.setSubmittedBy(value.getSubmittedBy());
                    ans.setValue(value.getValue());
                    ans.setUserId(value.getId());

                    answers.add(ans);
                }
                FieldAnswersResponse response = new FieldAnswersResponse();
                response.setField(fieldDto);
                response.setAnswers(answers);
                resultList.add(response);
            }
        }

        return resultList;
    }

    @Override
    public boolean validateField(String dataTypeName, String value, String optionsJson) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        if ("NUMBER".equalsIgnoreCase(dataTypeName)) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if ("DROPDOWN".equalsIgnoreCase(dataTypeName) || "MULTISELECT".equalsIgnoreCase(dataTypeName)) {
            if (optionsJson != null && !optionsJson.trim().isEmpty()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<String> opts = mapper.readValue(optionsJson, new TypeReference<List<String>>(){});
                    
                    if ("MULTISELECT".equalsIgnoreCase(dataTypeName)) {
                        List<String> userVals = mapper.readValue(value, new TypeReference<List<String>>(){});
                        for (String v : userVals) {
                            if (!opts.contains(v)) {
                                return false;
                            }
                        }
                        return true;
                    } else {
                        return opts.contains(value);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

}
