package com.envanter.inventory.client;

import com.envanter.inventory.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * UserClient'in RestTemplate tabanlı HTTP implementasyonu.
 *
 * <p>Constructor Injection — @Autowired yasak.
 * Hata durumunda null döner (fallback) → çağıran akış bozulmaz.</p>
 *
 * Çağrılan endpoint:
 * - GET {userServiceUrl}/api/users/{userId}
 */
@Component
public class UserClientImpl implements UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClientImpl.class);

    /** user-service base URL — docker-compose'da http://user-service:8081 */
    @Value("${user.service.url:http://user-service:8081}")
    private String userServiceUrl;

    private final RestTemplate restTemplate;

    public UserClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // -------------------------------------------------------------------------
    // UserClient impl
    // -------------------------------------------------------------------------

    /**
     * user-service'ten kullanıcı bilgisi getirir.
     * Hata (4xx / 5xx / zaman aşımı) → null döner, exception yutulmaz.
     *
     * @param userId Kullanıcı ID'si
     * @return UserDTO veya null (fallback)
     */
    @Override
    public UserDTO getUserById(Long userId) {
        String url = userServiceUrl + "/api/users/" + userId;

        log.debug("[UserClient] Kullanıcı bilgisi alınıyor → userId={}", userId);

        try {
            UserDTO user = restTemplate.getForObject(url, UserDTO.class);
            if (user != null) {
                log.debug("[UserClient] Kullanıcı bilgisi alındı → username={}", user.getUsername());
            }
            return user;
        } catch (RestClientException ex) {
            // Fallback: user-service erişilemiyor, null döndür
            log.warn("[UserClient] Kullanıcı bilgisi alınamadı → userId={}, hata={}",
                    userId, ex.getMessage());
            return null;
        }
    }
}
