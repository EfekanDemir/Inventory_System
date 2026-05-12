package com.envanter.user.security;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis tabanlı session yönetimi.
 * TTL: 30 dakika (session), 7 gün (refresh token).
 */
public interface RedisSessionService {

    /**
     * Yeni bir session oluşturur.
     *
     * @param token  JWT token (key)
     * @param userId Kullanıcı ID
     * @param expiry TTL süresi
     */
    void createSession(String token, Long userId, Duration expiry);

    /**
     * Token'a ait session verisini döner.
     */
    Optional<SessionData> getSession(String token);

    /**
     * Session'ı siler (logout).
     */
    void deleteSession(String token);

    /**
     * Basit session verisi tutucu.
     */
    class SessionData {
        private final Long userId;
        private final String token;

        public SessionData(Long userId, String token) {
            this.userId = userId;
            this.token = token;
        }

        public Long getUserId() { return userId; }
        public String getToken() { return token; }
    }
}
