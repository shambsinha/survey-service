package com.survey.survey_service.scheduler;

import com.survey.survey_service.entity.Form;
import com.survey.survey_service.repository.FieldRepository;
import com.survey.survey_service.repository.FormFieldMappingRepository;
import com.survey.survey_service.repository.FormRepository;
import com.survey.survey_service.repository.FormValueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TaskScheduler {

    private final FormRepository formRepository;
    private final FormValueRepository formValueRepository;
    private final FormFieldMappingRepository formFieldMappingRepository;
    private final FieldRepository fieldRepository;

    public TaskScheduler(FormRepository formRepository, FormValueRepository formValueRepository, FormFieldMappingRepository formFieldMappingRepository,
                         FieldRepository fieldRepository) {
        this.formRepository = formRepository;
        this.formValueRepository = formValueRepository;
        this.formFieldMappingRepository = formFieldMappingRepository;
        this.fieldRepository = fieldRepository;
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    public void deleteUnansweredForms() {
        List<Long> formIds = formRepository.findAllFormIds();

        List<Long> formIds2 = formValueRepository.findAllFormIdsInValues();

        List<Long> formIdsToBeDeleted = formIds.stream().filter(formId -> !formIds2.contains(formId)).toList();

        formRepository.deleteAllById(formIdsToBeDeleted);

        List<Long> fieldsIdsToBeDeleted = formFieldMappingRepository.findByIdsIn(formIdsToBeDeleted);

        fieldRepository.deleteAllById(fieldsIdsToBeDeleted);

    }
}
