package com.survey.survey_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.survey_service.dto.SessionUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
            
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {

            token = request.getHeader("token");
        }

        if (token != null && Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
            try {
                String sessionJson = (String) redisTemplate.opsForValue().get(token);
                if (sessionJson != null) {
                    SessionUser sessionUser = objectMapper.readValue(sessionJson, SessionUser.class);
                    
                    String role = sessionUser.getRole();
                    
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            sessionUser.getId(), null, Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to validate token: " + e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
