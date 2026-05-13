package com.envanter.user.util;

import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * SRP — Tek Sorumluluk: Kullanıcı giriş verisinin doğrulanması.
 *
 * <p>Tüm RegisterRequest ve LoginRequest doğrulama kuralları burada toplanır.
 * UserServiceImpl doğrulama detayını bilmez; yalnızca bu sınıfa delege eder.</p>
 */
@Component
public class UserValidator {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_USERNAME_LENGTH = 3;

    /**
     * Kayıt isteğini doğrular.
     *
     * @throws ValidationException herhangi bir kural ihlalinde
     */
    public void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new ValidationException("Kayıt isteği boş olamaz.");
        }

        validateUsername(request.getUsername());
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        validateName("Ad", request.getFirstName());
        validateName("Soyad", request.getLastName());
    }

    /**
     * Kullanıcı adı kuralları: min 3 karakter, harf/rakam/alt çizgi.
     */
    public void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Kullanıcı adı boş olamaz.");
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            throw new ValidationException(
                    "Kullanıcı adı en az " + MIN_USERNAME_LENGTH + " karakter olmalıdır.");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException(
                    "Kullanıcı adı yalnızca harf, rakam ve alt çizgi içerebilir.");
        }
    }

    /**
     * E-posta biçim doğrulaması.
     */
    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("E-posta boş olamaz.");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ValidationException("Geçersiz e-posta biçimi: " + email);
        }
    }

    /**
     * Şifre kuralları: min 8 karakter, en az 1 rakam.
     */
    public void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ValidationException("Şifre boş olamaz.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException(
                    "Şifre en az " + MIN_PASSWORD_LENGTH + " karakter olmalıdır.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Şifre en az bir rakam içermelidir.");
        }
    }

    private void validateName(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " alanı boş olamaz.");
        }
    }
}
