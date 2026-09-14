package com.survey.survey_service.controller;

import com.survey.survey_service.dto.FieldRequest;
import com.survey.survey_service.dto.FieldResponse;
import com.survey.survey_service.entity.Field;
import com.survey.survey_service.service.FieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fields")
public class FieldController {

    private final FieldService fieldService;

    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @PostMapping("/system")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Field> createSystemField(@RequestBody FieldRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Field savedField = fieldService.createSystemField(request, userId);
        return ResponseEntity.ok(savedField);
    }

    @GetMapping("/system")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<FieldResponse>> getSystemFields() {
        return ResponseEntity.ok(fieldService.getSystemFields());
    }
}
