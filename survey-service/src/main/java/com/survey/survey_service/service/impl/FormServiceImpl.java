package com.survey.survey_service.service.impl;

import com.survey.survey_service.dto.*;
import com.survey.survey_service.enums.DataType;
import com.survey.survey_service.entity.*;
import com.survey.survey_service.repository.*;
import com.survey.survey_service.service.FormService;
import com.survey.survey_service.validator.FieldValidatorMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import com.survey.survey_service.validator.FieldValidator;

@Service
public class FormServiceImpl implements FormService {

    private final FormRepository formRepository;
    private final FieldRepository fieldRepository;
    private final FormFieldMappingRepository formFieldMappingRepository;
    private final FormValueRepository formValueRepository;
    private final FieldValidatorMap validatorMap;
    
    public FormServiceImpl(FormRepository formRepository, FieldRepository fieldRepository, FormFieldMappingRepository formFieldMappingRepository, FormValueRepository formValueRepository, FieldValidatorMap validatorMap) {
        this.formRepository = formRepository;
        this.fieldRepository = fieldRepository;
        this.formFieldMappingRepository = formFieldMappingRepository;
        this.formValueRepository = formValueRepository;
        this.validatorMap=validatorMap;
    }

    @Override
    @Transactional
    public void deleteUnansweredForms() {
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        List<Long> toDelete = formRepository.findUnansweredFormIdsCreatedBefore(thirtyDaysAgo);

        if (!toDelete.isEmpty()) {
            List<Long> fieldsToDelete = formFieldMappingRepository.findByIdsIn(toDelete);
            formRepository.deleteAllById(toDelete);
            if (fieldsToDelete != null && !fieldsToDelete.isEmpty()) {
                fieldRepository.deleteCustomFieldsByIds(fieldsToDelete);
            }
        }
    }

    @Override
    @Transactional
    public FormResponse createForm(FormCreateRequest request) {
        Form form = saveNewForm(request);
        List<FormFieldMapping> mappings = new ArrayList<>();
        
        List<Field> defaultFields = fieldRepository.findByIsDefaultTrue();
        mappings.addAll(createMappings(form.getId(), request.getUserId(), defaultFields, 1, true));
        
        if (request.getFields() != null && !request.getFields().isEmpty()) {
            List<Field> customFields = saveCustomFields(request);
            mappings.addAll(createCustomMappings(form.getId(), request, customFields, defaultFields.size() + 1));
        }

        formFieldMappingRepository.saveAll(mappings);
        return buildFormResponse(form, mappings);
    }

    @Override
    public FormResponse getForm(Long formId) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
        return buildFormResponse(form, formFieldMappingRepository.findByFormId(formId));
    }

    @Override
    @Transactional
    public void submitForm(Long formId, FormSubmissionRequest request, Long uId) {
        List<FormFieldMapping> mappings = formFieldMappingRepository.findByFormId(formId);
        Map<Long, String> submittedValues = request.getValues() != null ? request.getValues() : new HashMap<>();
        
        validateRequiredFields(mappings, submittedValues);

        if (!submittedValues.isEmpty()) {
            Map<Long, Field> fields = fetchFields(new ArrayList<>(submittedValues.keySet()));
            List<FormValue> vals = createFormValues(formId, uId, submittedValues, fields);
            formValueRepository.saveAll(vals);
        }
    }

    @Override
    public Page<FieldAnswersResponse> getFormResults(Long formId, Long requestingUserId, Pageable pageable) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
        if (!form.getCreatedBy().equals(requestingUserId)) {
            throw new RuntimeException("Unauthorized: You do not have permission to view these results");
        }
        
        Page<FormFieldMapping> mappingPage = formFieldMappingRepository.findByFormId(formId, pageable);
        List<FormFieldMapping> mappings = mappingPage.getContent();
        
        List<Long> fieldIds = mappings.stream().map(FormFieldMapping::getFieldId).toList();
        Map<Long, List<FormValue>> valuesByFieldId = new HashMap<>();
        
        if (!fieldIds.isEmpty()) {
            valuesByFieldId = formValueRepository.findByFormIdAndFieldIdIn(formId, fieldIds).stream()
                    .collect(Collectors.groupingBy(FormValue::getFieldId));
        }

        Map<Long, Field> fields = fetchFields(fieldIds);
        List<FieldAnswersResponse> results = new ArrayList<>();
        
        for (FormFieldMapping mapping : mappings) {
            Field field = fields.get(mapping.getFieldId());
            if (field != null) {
                results.add(buildFieldAnswersResponse(field, mapping, valuesByFieldId.getOrDefault(field.getId(), new ArrayList<>())));
            }
        }
        
        return new PageImpl<>(results, pageable, mappingPage.getTotalElements());
    }

    private Form saveNewForm(FormCreateRequest request) {
        Form form = new Form();
        form.setCreatedBy(request.getUserId());
        form.setUpdatedBy(request.getUserId());
        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());
        return formRepository.save(form);
    }

    private List<Field> saveCustomFields(FormCreateRequest request) {
        List<Field> processedFields = new ArrayList<>();
        List<Field> toSave = new ArrayList<>();
        for (FieldRequest fr : request.getFields()) {
            if (fr.getId() != null) {
                Field systemField = fieldRepository.findById(fr.getId())
                        .orElseThrow(() -> new RuntimeException("System field not found with ID: " + fr.getId()));
                if (!Boolean.TRUE.equals(systemField.getIsSystem())) {
                    throw new RuntimeException("Field is not a system field");
                }
                processedFields.add(systemField);
            } else {
                Field field = new Field();
                field.setDataType(DataType.valueOf(fr.getDataType().toUpperCase()));
                field.setName(fr.getName());
                field.setLabel(fr.getLabel());
                field.setOptionsJson(fr.getOptionsJson());
                field.setIsDefault(false);
                field.setCreatedBy(request.getUserId());
                field.setUpdatedBy(request.getUserId());
                toSave.add(field);
                processedFields.add(field);
            }
        }
        if (!toSave.isEmpty()) {
            fieldRepository.saveAll(toSave);
        }
        return processedFields;
    }

    private List<FormFieldMapping> createMappings(Long formId, Long userId, List<Field> fields, int startOrder, boolean isRequired) {
        List<FormFieldMapping> mappings = new ArrayList<>();
        int order = startOrder;
        for (Field field : fields) {
            FormFieldMapping mapping = new FormFieldMapping();
            mapping.setFormId(formId);
            mapping.setFieldId(field.getId());
            mapping.setIsRequired(isRequired);
            mapping.setDisplayOrder(order++);
            mapping.setCreatedBy(userId);
            mapping.setUpdatedBy(userId);
            mappings.add(mapping);
        }
        return mappings;
    }

    private List<FormFieldMapping> createCustomMappings(Long formId, FormCreateRequest request, List<Field> savedFields, int startOrder) {
        List<FormFieldMapping> mappings = new ArrayList<>();
        int order = startOrder;
        for (int i = 0; i < savedFields.size(); i++) {
            FieldRequest fr = request.getFields().get(i);
            FormFieldMapping mapping = new FormFieldMapping();
            mapping.setFormId(formId);
            mapping.setFieldId(savedFields.get(i).getId());
            mapping.setIsRequired(fr.getIsRequired() != null ? fr.getIsRequired() : false);
            mapping.setDisplayOrder(fr.getDisplayOrder() != null ? fr.getDisplayOrder() : order++);
            mapping.setCreatedBy(request.getUserId());
            mapping.setUpdatedBy(request.getUserId());
            mappings.add(mapping);
        }
        return mappings;
    }

    private FormResponse buildFormResponse(Form form, List<FormFieldMapping> mappings) {
        Map<Long, Field> fields = fetchFields(mappings.stream().map(FormFieldMapping::getFieldId).toList());
        List<FieldResponse> resList = new ArrayList<>();
        
        for (FormFieldMapping mapping : mappings) {
            Field field = fields.get(mapping.getFieldId());
            if (field != null) {
                resList.add(toResponse(field, mapping));
            }
        }

        FormResponse response = new FormResponse();
        response.setId(form.getId());
        response.setTitle(form.getTitle());
        response.setDescription(form.getDescription());
        response.setFields(resList);
        return response;
    }

    private void validateRequiredFields(List<FormFieldMapping> mappings, Map<Long, String> submittedValues) {
        for (FormFieldMapping mapping : mappings) {
            if (Boolean.TRUE.equals(mapping.getIsRequired())) {
                String val = submittedValues.get(mapping.getFieldId());
                if (val == null || val.trim().isEmpty()) {
                    throw new RuntimeException("Required field missing: " + mapping.getFieldId());
                }
            }
        }
    }

    private List<FormValue> createFormValues(Long formId, Long uId, Map<Long, String> submittedValues, Map<Long, Field> fields) {
        List<FormValue> vals = new ArrayList<>();
        for (Map.Entry<Long, String> entry : submittedValues.entrySet()) {
            Field field = fields.get(entry.getKey());
            if (field == null) throw new RuntimeException("field not found");

            FieldValidator validator = validatorMap.getValidator(field.getDataType());
            boolean isValid = validator != null ? validator.validate(entry.getValue(), field.getOptionsJson()) : true;

            if (!isValid) throw new RuntimeException("invalid data type");

            FormValue formValue = new FormValue();
            formValue.setSubmittedBy(uId);
            formValue.setFormId(formId);
            formValue.setFieldId(field.getId());
            formValue.setValue(entry.getValue());
            formValue.setCreatedBy(uId);
            formValue.setUpdatedBy(uId);
            vals.add(formValue);
        }
        return vals;
    }

    private FieldAnswersResponse buildFieldAnswersResponse(Field field, FormFieldMapping mapping, List<FormValue> fieldValues) {
        FieldAnswersResponse response = new FieldAnswersResponse();
        response.setField(toResponse(field, mapping));

        List<AnswerDetail> answers = new ArrayList<>();
        for (FormValue value : fieldValues) {
            AnswerDetail ans = new AnswerDetail();
            ans.setSubmittedBy(value.getSubmittedBy());
            ans.setValue(value.getValue());
            ans.setUserId(value.getId());
            answers.add(ans);
        }
        response.setAnswers(answers);
        return response;
    }

    private Map<Long, Field> fetchFields(List<Long> fieldIds) {
        return fieldRepository.findAllById(fieldIds).stream()
                .collect(Collectors.toMap(Field::getId, Function.identity()));
    }

    private FieldResponse toResponse(Field field, FormFieldMapping mapping) {
        FieldResponse fr = new FieldResponse();
        fr.setFieldId(field.getId());
        fr.setName(field.getName());
        fr.setLabel(field.getLabel());
        fr.setDataType(field.getDataType().name());
        fr.setOptionsJson(field.getOptionsJson());
        fr.setIsRequired(mapping.getIsRequired());
        fr.setDisplayOrder(mapping.getDisplayOrder());
        return fr;
    }
}