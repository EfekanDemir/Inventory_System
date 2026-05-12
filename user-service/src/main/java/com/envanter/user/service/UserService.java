package com.envanter.user.service;

import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;

/**
 * UserService arayüzü — kayıt ve giriş işlemlerini tanımlar.
 */
public interface UserService {

    /**
     * Yeni kullanıcı kaydeder.
     *
     * @param request Kayıt isteği (username, email, password, ...)
     * @return Oluşturulan kullanıcının DTO'su
     * @throws com.envanter.user.exception.ConflictException kullanıcı adı veya email zaten varsa
     */
    UserDTO register(RegisterRequest request);
}
