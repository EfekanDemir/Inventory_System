package com.envanter.unit;

import com.envanter.user.dto.LoginRequest;
import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;
import com.envanter.user.exception.ConflictException;
import com.envanter.user.exception.UnauthorizedException;
import com.envanter.user.model.User;
import com.envanter.user.repository.UserRepository;
import com.envanter.user.security.JwtTokenProvider;
import com.envanter.user.security.RedisSessionService;
import com.envanter.user.service.UserServiceImpl;
import com.envanter.user.util.UserMapper;
import com.envanter.user.util.UserValidator;
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
import static org.mockito.Mockito.*;

/**
 * UserService - Kayit ve Giris Testleri
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Kayit ve Giris Testleri")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisSessionService redisSessionService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserServiceImpl.PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Gecerli istek ile kayit - UserDTO doner")
    void register_WithValidRequest_ReturnsUserDTO() {
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

        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("oktaytest");
        when(userMapper.toDTO(any(User.class))).thenReturn(dto);

        UserDTO result = userService.register(request);

        assertNotNull(result);
        assertEquals("oktaytest", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Gecerli kimlik bilgileriyle giris - UserDTO icinde token doner")
    void login_WithValidCredentials_ReturnsUserDTOWithToken() {
        LoginRequest request = new LoginRequest("oktaytest", "SecurePass123!");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oktaytest");
        existingUser.setPasswordHash("$2a$10$hashedPassword");
        existingUser.setActive(true);

        when(userRepository.findByUsername("oktaytest")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("SecurePass123!", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(existingUser)).thenReturn("mocktoken");

        UserDTO dto = new UserDTO();
        dto.setUsername("oktaytest");
        when(userMapper.toDTO(existingUser)).thenReturn(dto);

        UserDTO result = userService.login(request);

        assertNotNull(result);
        assertEquals("mocktoken", result.getToken());
        assertEquals("oktaytest", result.getUsername());

        verify(jwtTokenProvider, times(1)).generateToken(existingUser);
    }

    @Test
    @DisplayName("Yanlis sifre ile giris - UnauthorizedException firlatir")
    void login_WithInvalidPassword_ThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest("oktaytest", "YanlisParola!");

        User existingUser = new User();
        existingUser.setUsername("oktaytest");
        existingUser.setPasswordHash("$2a$10$hashedPassword");
        existingUser.setActive(true);

        when(userRepository.findByUsername("oktaytest")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("YanlisParola!", "$2a$10$hashedPassword")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.login(request));
    }
}
