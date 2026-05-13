package com.envanter.user.controller;

import com.envanter.user.dto.LoginRequest;
import com.envanter.user.dto.LoginResponse;
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
 *
 * <p>DIP: UserService arayüzüne bağımlı — implementasyon detayını bilmez.
 * Constructor Injection zorunlu — @Autowired field injection yasak.</p>
 *
 * Endpointler:
 * - POST /api/users/register → yeni kullanıcı kaydı
 * - POST /api/users/login    → JWT token üretimi
 * - GET  /api/users/health   → servis sağlık kontrolü
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /** DIP: Somut impl değil, arayüz inject edildi. */
    private final UserService userService;

    /** Constructor Injection — Spring tek constructor varsa @Autowired gerekmez. */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // Endpoints
    // -------------------------------------------------------------------------

    /**
     * Yeni kullanıcı kaydı.
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        log.info("Kayıt isteği: username={}", request.getUsername());
        UserDTO created = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Kullanıcı girişi ve JWT token üretimi.
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Giriş isteği: username={}", request.getUsername());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Servis sağlık kontrolü.
     * GET /api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("user-service UP");
    }
}
