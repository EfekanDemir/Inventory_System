package com.envanter.user.controller;

import com.envanter.common.generic.GenericResponseWrapper;
import com.envanter.user.dto.LoginRequest;
import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;
import com.envanter.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kullanıcı kimlik doğrulama ve yönetim controller'ı.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Yeni kullanıcı kaydı.
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<GenericResponseWrapper<UserDTO>> register(@RequestBody RegisterRequest request) {
        log.info("Kayıt isteği: username={}", request.getUsername());
        UserDTO created = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GenericResponseWrapper.success(created, "Kayıt başarılı."));
    }

    /**
     * Kullanıcı girişi ve JWT token üretimi.
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<GenericResponseWrapper<UserDTO>> login(@RequestBody LoginRequest request) {
        log.info("Giriş isteği: username={}", request.getUsername());
        UserDTO response = userService.login(request);
        return ResponseEntity.ok(GenericResponseWrapper.success(response, "Giriş başarılı."));
    }

    /**
     * Servis sağlık kontrolü.
     * GET /api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<GenericResponseWrapper<String>> health() {
        return ResponseEntity.ok(GenericResponseWrapper.success("user-service UP"));
    }
}
