package com.envanter.user.security;

import com.envanter.user.model.User;

/**
 * JWT token işlemlerini yöneten servis.
 * Gerçek implementasyon: jjwt 0.12.x ile yapılacak.
 */
public interface JwtTokenProvider {

    /**
     * Verilen User nesnesine ait JWT token oluşturur.
     */
    String generateToken(User user);

    /**
     * Token geçerliliğini kontrol eder.
     */
    boolean validateToken(String token);

    /**
     * Token'dan userId çıkarır.
     */
    Long getUserIdFromToken(String token);
}
