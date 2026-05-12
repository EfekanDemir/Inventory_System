package com.envanter.user.service;

import com.envanter.user.model.SessionData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisSessionService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String SESSION_PREFIX = "session:";

    public void createSession(String token, Long userId, Duration expiry) {
        try {
            SessionData sessionData = new SessionData(userId, token);
            String sessionJson = objectMapper.writeValueAsString(sessionData);
            stringRedisTemplate.opsForValue().set(SESSION_PREFIX + token, sessionJson, expiry);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Session JSON parsing error", e);
        }
    }

    public Optional<SessionData> getSession(String token) {
        String sessionJson = stringRedisTemplate.opsForValue().get(SESSION_PREFIX + token);
        if (sessionJson == null) {
            return Optional.empty();
        }
        
        try {
            SessionData sessionData = objectMapper.readValue(sessionJson, SessionData.class);
            return Optional.of(sessionData);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public void deleteSession(String token) {
        stringRedisTemplate.delete(SESSION_PREFIX + token);
    }
}
