package com.survey.survey_service.service.impl;

import com.survey.survey_service.dto.RoleAssignRequest;
import com.survey.survey_service.entity.User;
import com.survey.survey_service.enums.Role;
import com.survey.survey_service.repository.UserRepository;
import com.survey.survey_service.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void assignRole(Long userId, RoleAssignRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            Role role = Role.valueOf(request.getRole().name());
            user.setRole(role.name());
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role");
        }
    }
}
