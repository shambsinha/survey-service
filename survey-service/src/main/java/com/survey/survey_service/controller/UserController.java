package com.survey.survey_service.controller;

import com.survey.survey_service.dto.RoleAssignRequest;
import com.survey.survey_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> assignRole(@PathVariable Long userId, @RequestBody RoleAssignRequest request) {
        userService.assignRole(userId, request);
        return ResponseEntity.ok("Role assigned");
    }
}
