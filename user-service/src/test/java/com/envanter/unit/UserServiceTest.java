package com.envanter.unit;

import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;
import com.envanter.user.exception.ConflictException;
import com.envanter.user.repository.UserRepository;
import com.envanter.user.security.JwtTokenProvider;
import com.envanter.user.security.RedisSessionService;
import com.envanter.user.service.UserServiceImpl;
import com.envanter.user.model.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * TDD — RED Aşaması
 *
 * UserServiceImpl henüz implemente edilmediği için bu testler BAŞARISIZ olacak.
 * Amaç: compile olabilir ama runtime'da UnsupportedOperationException fırlatır.
 *
 * Sonraki adım: GREEN — register() metodu implemente edilecek.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Kayıt (Register) Testleri")
class UserServiceTest {

    // ── Mock'lar ────────────────────────────────────────────────────────────────

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisSessionService redisSessionService;

    @Mock
    private UserServiceImpl.PasswordEncoderPort passwordEncoder;

    // ── Test altındaki sınıf ─────────────────────────────────────────────────

    @InjectMocks
    private UserServiceImpl userService;

    // ── Test Metodları ────────────────────────────────────────────────────────

    /**
     * Senaryo 1 — Geçerli kayıt isteği başarıyla tamamlanmalı
     *
     * ⚠️ RED: UnsupportedOperationException nedeniyle BAŞARISIZ olacak.
     */
    @Test
    @DisplayName("Geçerli istek ile kayıt — UserDTO döner")
    void register_WithValidRequest_ReturnsUserDTO() {
        // ── Given ──────────────────────────────────────────────────────────────
        RegisterRequest request = new RegisterRequest(
                "oktaytest",
                "oktay@envanter.com",
                "SecurePass123!",
                "Oktay",
                "Test"
        );

        // Kullanıcı adı ve email henüz sistemde yok
        when(userRepository.existsByUsername("oktaytest")).thenReturn(false);
        when(userRepository.existsByEmail("oktay@envanter.com")).thenReturn(false);

        // Şifre hash'leme mock'u
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashedPassword");

        // Kaydedilen user mock'u
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("oktaytest");
        savedUser.setEmail("oktay@envanter.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // ── When ───────────────────────────────────────────────────────────────
        UserDTO result = userService.register(request);

        // ── Then ───────────────────────────────────────────────────────────────
        assertNotNull(result, "Kayıt sonucu null olmamalı");
        assertEquals("oktaytest", result.getUsername(),
                "Dönen DTO'nun username'i istek ile eşleşmeli");
        assertThat(result.getId()).isNotNull();

        // Repository save bir kez çağrılmalı
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Senaryo 2 — Var olan kullanıcı adı ile kayıt → ConflictException fırlatılmalı
     *
     * ⚠️ RED: UnsupportedOperationException fırlattığı için assertThrows yanlış
     *         exception alacak → test BAŞARISIZ olacak.
     */
    @Test
    @DisplayName("Mevcut kullanıcı adı ile kayıt — ConflictException fırlatır")
    void register_WithDuplicateUsername_ThrowsConflictException() {
        // ── Given ──────────────────────────────────────────────────────────────
        RegisterRequest request = new RegisterRequest(
                "varolan_kullanici",
                "yeni@envanter.com",
                "Pass123!",
                "Ad",
                "Soyad"
        );

        // Kullanıcı adı sistemde zaten mevcut
        when(userRepository.existsByUsername("varolan_kullanici")).thenReturn(true);

        // ── When & Then ────────────────────────────────────────────────────────
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.register(request),
                "Var olan kullanıcı adı için ConflictException fırlatılmalı"
        );

        assertThat(exception.getMessage())
                .containsIgnoringCase("kullanıcı")
                .as("Hata mesajı kullanıcı adına değinmeli");

        // Kayıt çağrısı HİÇ yapılmamalı
        verify(userRepository, never()).save(any(User.class));
    }
}
