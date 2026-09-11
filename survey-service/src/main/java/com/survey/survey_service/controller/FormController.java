package com.survey.survey_service.controller;

import com.survey.survey_service.dto.FormCreateRequest;
import com.survey.survey_service.dto.FormResponse;
import com.survey.survey_service.service.FormService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;
    private final RedisTemplate<String, Object> redisTemplate;

    public FormController(FormService formService, RedisTemplate<String, Object> redisTemplate) {
        this.formService = formService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createForm(@RequestBody FormCreateRequest request, 
                                        @RequestHeader(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login first to get a token");
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(token))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }FormResponse response = formService.createForm(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<FormResponse> getForm(@PathVariable Long id) {
        FormResponse response = formService.getForm(id);
        return ResponseEntity.ok(response);
    }
}
