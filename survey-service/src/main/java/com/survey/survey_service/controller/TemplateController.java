package com.survey.survey_service.controller;

import com.survey.survey_service.dto.FieldResponse;
import com.survey.survey_service.dto.FormCreateRequest;
import com.survey.survey_service.dto.FormResponse;
import com.survey.survey_service.dto.FormUpdateRequest;
import com.survey.survey_service.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/template")
public class TemplateController {

    private final FormService formService;

    public TemplateController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping
    @PreAuthorize("hasRole(Role.ADMIN)")
    public ResponseEntity<FormResponse> createTemplate(@RequestBody FormCreateRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        request.setUserId(userId);
        request.setIsTemplate(true);
        FormResponse response = formService.createForm(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FormResponse>> getTemplateForms() {
        List<FormResponse> responses = formService.getTemplateForms();
        responses.forEach(r -> r.getFields().sort(Comparator.comparing(FieldResponse::getDisplayOrder)));
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(Role.ADMIN)")
    public ResponseEntity<FormResponse> updateTemplate(@PathVariable Long id, @RequestBody FormUpdateRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FormResponse response = formService.updateForm(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(Role.ADMIN)")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        formService.deleteForm(id, userId);
        return ResponseEntity.ok().build();
    }
}
