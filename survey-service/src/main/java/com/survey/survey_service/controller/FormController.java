package com.survey.survey_service.controller;

import com.survey.survey_service.dto.FormCreateRequest;
import com.survey.survey_service.dto.FormResponse;
import com.survey.survey_service.dto.FieldAnswersResponse;
import com.survey.survey_service.dto.FormSubmissionRequest;
import com.survey.survey_service.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/form")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping
    public ResponseEntity<FormResponse> createForm(@RequestBody FormCreateRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        request.setUserId(userId);
        FormResponse response = formService.createForm(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FormResponse> getForm(@PathVariable Long id) {
        FormResponse response = formService.getForm(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit/{formId}")
    public ResponseEntity<Void> submitForm(@PathVariable Long formId, @RequestBody FormSubmissionRequest request) {
        Long uId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        formService.submitForm(formId, request, uId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{formId}/results")
    public ResponseEntity<Page<FieldAnswersResponse>> getFormResults(
            @PathVariable Long formId,
            Pageable pageable) {
        Long uId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(formService.getFormResults(formId, uId, pageable));
    }
}