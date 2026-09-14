package com.survey.survey_service.service.impl;

import com.survey.survey_service.dto.*;
import com.survey.survey_service.entity.User;
import com.survey.survey_service.enums.Role;
import com.survey.survey_service.repository.UserRepository;
import com.survey.survey_service.service.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthServiceImpl(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<User> userdetails = userRepository.findByUsername(request.getUsername());
        if (userdetails.isPresent()) {
            User user = userdetails .get();
            if (user.getPassword().equals(request.getPassword())) {
                String token = UUID.randomUUID().toString();
                
                SessionUser sessionUser = new SessionUser(user.getId(), user.getUsername(), user.getRole());
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String sessionJson = mapper.writeValueAsString(sessionUser);
                    redisTemplate.opsForValue().set(token, sessionJson, 30, TimeUnit.DAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to create session: " + e.getMessage());
                }

                LoginResponse response = new LoginResponse();
                response.setToken(token);
                response.setMessage("Login successful");
                return response;
            }
        }
        
        throw new RuntimeException("Invalid username or password");
    }
    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER.name());
        userRepository.save(user);
    }
}
