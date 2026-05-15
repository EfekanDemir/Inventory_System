package com.envanter.user.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import org.springframework.lang.NonNull;

@Service
public class RedisSessionServiceImpl implements RedisSessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSessionServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void createSession(@NonNull String token, @NonNull Long userId, @NonNull Duration expiry) {
        redisTemplate.opsForValue().set(token, userId, expiry);
    }

    @Override
    public Optional<SessionData> getSession(@NonNull String token) {
        Object val = redisTemplate.opsForValue().get(token);
        if (val == null) return Optional.empty();
        
        Long userId;
        if (val instanceof Integer) {
            userId = ((Integer) val).longValue();
        } else if (val instanceof Long) {
            userId = (Long) val;
        } else {
            return Optional.empty();
        }
        
        return Optional.of(new SessionData(userId, token));
    }

    @Override
    public void deleteSession(@NonNull String token) {
        redisTemplate.delete(token);
    }
}
