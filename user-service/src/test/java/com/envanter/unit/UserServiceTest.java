package com.envanter.unit;

import com.envanter.user.dto.LoginRequest;
import com.envanter.user.dto.LoginResponse;
import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;
import com.envanter.user.exception.ConflictException;
import com.envanter.user.exception.ResourceNotFoundException;
import com.envanter.user.exception.UnauthorizedException;
import com.envanter.user.model.User;
import com.envanter.user.repository.UserRepository;
import com.envanter.user.security.JwtTokenProvider;
import com.envanter.user.security.RedisSessionService;
import com.envanter.user.service.UserServiceImpl;

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
 * TDD -- RED Asama
 *
 * UserServiceImpl henuz implemente edilmedigi icin bu testler BASARISIZ olacak.
 * Amac: compile olabilir ama runtime'da UnsupportedOperationException firlatir.
 *
 * Commit 2: register testleri (RED)
 * Commit 3: login testleri (RED)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Kayit ve Giris Testleri")
class UserServiceTest {

    // -- Mock'lar --

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisSessionService redisSessionService;

    @Mock
    private UserServiceImpl.PasswordEncoderPort passwordEncoder;

    // -- Test altindaki sinif --

    @InjectMocks
    private UserServiceImpl userService;

    // ===========================================================================
    // REGISTER TESTLERI -- Commit 2 (RED)
    // ===========================================================================

    /**
     * Senaryo 1 - Gecerli kayit istegi basariyla tamamlanmali
     * RED: UnsupportedOperationException nedeniyle BASARISIZ olacak.
     */
    @Test
    @DisplayName("Gecerli istek ile kayit - UserDTO doner")
    void register_WithValidRequest_ReturnsUserDTO() {
        // -- Given --
        RegisterRequest request = new RegisterRequest(
                "oktaytest",
                "oktay@envanter.com",
                "SecurePass123!",
                "Oktay",
                "Test"
        );

        when(userRepository.existsByUsername("oktaytest")).thenReturn(false);
        when(userRepository.existsByEmail("oktay@envanter.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("$2a$10$hashedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("oktaytest");
        savedUser.setEmail("oktay@envanter.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // -- When --
        UserDTO result = userService.register(request);

        // -- Then --
        assertNotNull(result, "Kayit sonucu null olmamali");
        assertEquals("oktaytest", result.getUsername());
        assertThat(result.getId()).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Senaryo 2 - Var olan kullanici adi ile kayit -> ConflictException
     * RED: UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Mevcut kullanici adi ile kayit - ConflictException firlatir")
    void register_WithDuplicateUsername_ThrowsConflictException() {
        // -- Given --
        RegisterRequest request = new RegisterRequest(
                "varolan_kullanici",
                "yeni@envanter.com",
                "Pass123!",
                "Ad",
                "Soyad"
        );

        when(userRepository.existsByUsername("varolan_kullanici")).thenReturn(true);

        // -- When & Then --
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.register(request),
                "Var olan kullanici adi icin ConflictException firlatilmali"
        );

        assertThat(exception.getMessage()).isNotNull();
        verify(userRepository, never()).save(any(User.class));
    }

    // ===========================================================================
    // LOGIN TESTLERI -- Commit 3 (RED)
    // ===========================================================================

    /**
     * Senaryo 3 - Gecerli kimlik bilgileriyle giris -> token doner
     * RED: login() UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Gecerli kimlik bilgileriyle giris - LoginResponse icinde token doner")
    void login_WithValidCredentials_ReturnsToken() {
        // -- Given --
        LoginRequest request = new LoginRequest("oktaytest", "SecurePass123!");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oktaytest");
        existingUser.setEmail("oktay@envanter.com");
        existingUser.setPasswordHash("$2a$10$hashedPassword");
        existingUser.setActive(true);

        when(userRepository.findByUsername("oktaytest")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("SecurePass123!", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(existingUser))
                .thenReturn("eyJhbGciOiJIUzI1NiJ9.mocktoken");

        // -- When --
        LoginResponse result = userService.login(request);

        // -- Then --
        assertNotNull(result, "Login sonucu null olmamali");
        assertNotNull(result.getToken(), "JWT token null olmamali");
        assertThat(result.getToken()).isNotBlank();
        assertEquals("oktaytest", result.getUsername());

        verify(userRepository, times(1)).findByUsername("oktaytest");
        verify(jwtTokenProvider, times(1)).generateToken(existingUser);
    }

    /**
     * Senaryo 4 - Gecerli kullanici adi ama yanlis sifre -> UnauthorizedException
     * RED: login() UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Yanlis sifre ile giris - UnauthorizedException firlatir")
    void login_WithInvalidPassword_ThrowsUnauthorizedException() {
        // -- Given --
        LoginRequest request = new LoginRequest("oktaytest", "YanlisParola!");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oktaytest");
        existingUser.setPasswordHash("$2a$10$hashedPassword");
        existingUser.setActive(true);

        when(userRepository.findByUsername("oktaytest")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("YanlisParola!", "$2a$10$hashedPassword")).thenReturn(false);

        // -- When & Then --
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> userService.login(request),
                "Yanlis sifre icin UnauthorizedException firlatilmali"
        );

        assertThat(exception.getMessage()).isNotNull();

        verify(userRepository, times(1)).findByUsername("oktaytest");
        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }

    /**
     * Senaryo 5 - Sistemde olmayan kullanici adi -> ResourceNotFoundException
     * RED: login() UnsupportedOperationException firlatir -> BASARISIZ olacak.
     */
    @Test
    @DisplayName("Var olmayan kullanici ile giris - ResourceNotFoundException firlatir")
    void login_WithNonExistentUser_ThrowsNotFoundException() {
        // -- Given --
        LoginRequest request = new LoginRequest("olmayan_kullanici", "HerhangiParola!");

        when(userRepository.findByUsername("olmayan_kullanici")).thenReturn(Optional.empty());

        // -- When & Then --
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.login(request),
                "Var olmayan kullanici icin ResourceNotFoundException firlatilmali"
        );

        assertThat(exception.getMessage()).isNotNull();

        verify(userRepository, times(1)).findByUsername("olmayan_kullanici");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }
}
