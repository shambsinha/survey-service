package com.survey.survey_service.service.impl;

import com.survey.survey_service.dto.LoginRequest;
import com.survey.survey_service.dto.LoginResponse;
import com.survey.survey_service.entity.User;
import com.survey.survey_service.repository.UserRepository;
import com.survey.survey_service.service.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
                redisTemplate.opsForValue().set(token, user.getId().toString(), 30, TimeUnit.DAYS);
                LoginResponse response = new LoginResponse();
                response.setToken(token);
                response.setMessage("Login successful");
                return response;
            }
        }
        
        throw new RuntimeException("Invalid username or password");
    }
}
