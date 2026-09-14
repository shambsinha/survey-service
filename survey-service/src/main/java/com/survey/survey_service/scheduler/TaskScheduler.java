package com.survey.survey_service.scheduler;

import com.survey.survey_service.service.FormService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskScheduler {

    private final FormService formService;

    public TaskScheduler(FormService formService) {
        this.formService = formService;
    }

    @Scheduled(cron = "${cron.delete-forms}")
    public void deleteUnansweredForms() {
        formService.deleteUnansweredForms();
    }
}
