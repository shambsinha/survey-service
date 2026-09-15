package com.survey.survey_service.controller;

import com.survey.survey_service.dto.*;
import com.survey.survey_service.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Comparator;
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
        request.setIsTemplate(false);
        FormResponse response = formService.createForm(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FormResponse> getForm(@PathVariable Long id) {
        FormResponse response = formService.getForm(id);
        response.getFields().sort(Comparator.comparing(FieldResponse::getDisplayOrder));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormResponse> updateForm(@PathVariable Long id, @RequestBody FormUpdateRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FormResponse response = formService.updateForm(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        formService.deleteForm(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/use-template/{templateId}")
    public ResponseEntity<FormResponse> createFromTemplate(@PathVariable Long templateId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FormResponse response = formService.createFormFromTemplate(templateId, userId);
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