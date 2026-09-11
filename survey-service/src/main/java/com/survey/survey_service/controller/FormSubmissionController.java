package com.survey.survey_service.controller;

import com.survey.survey_service.dto.FieldAnswersResponse;
import com.survey.survey_service.dto.FormSubmissionRequest;
import com.survey.survey_service.service.FormService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.survey.survey_service.enums.Sort;

@RestController
@RequestMapping("/api/submissions")
public class FormSubmissionController {

    private final FormService formService;
    private final org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    public FormSubmissionController(FormService formService, RedisTemplate<String, Object> redisTemplate) {
        this.formService = formService;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/{formId}")
    public ResponseEntity<?> submitForm(@PathVariable Long formId, 
                                        @RequestBody FormSubmissionRequest request,
                                        @RequestHeader(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login first to get a token");
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(token))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        
        String uIdStr = (String) redisTemplate.opsForValue().get(token);
        Long uId = Long.parseLong(uIdStr);
        
        formService.submitForm(formId, request, uId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{formId}/results")
    public ResponseEntity<List<FieldAnswersResponse>> getFormResults(@PathVariable Long formId) {
        return ResponseEntity.ok(formService.getFormResults(formId));
    }
}
