package com.envanter.inventory.client;

import com.envanter.inventory.dto.UserDTO;

/**
 * user-service ile iletişim için REST client arayüzü.
 *
 * <p>Implementasyonu: {@link UserClientImpl}
 * Test sırasında Mockito ile mock'lanır — gerçek HTTP çağrısı yapılmaz.</p>
 */
public interface UserClient {

    /**
     * user-service'ten kullanıcı bilgisi getirir.
     *
     * @param userId Kullanıcı ID'si
     * @return UserDTO (bulunamazsa null, servis kapalıysa null — fallback)
     */
    UserDTO getUserById(Long userId);
}
