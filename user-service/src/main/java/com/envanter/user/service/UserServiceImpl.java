package com.envanter.user.service;

import com.envanter.user.dto.LoginRequest;
import com.envanter.user.dto.LoginResponse;
import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;
import com.envanter.user.repository.UserRepository;
import com.envanter.user.security.JwtTokenProvider;
import com.envanter.user.security.RedisSessionService;

/**
 * UserService implementasyonu.
 *
 * ⚠️ TDD RED AŞAMASI ⚠️
 * Bu sınıf kasıtlı olarak BOŞTUR — implementasyon henüz yapılmamıştır.
 * UserServiceTest'teki testler bu yüzden BAŞARISIZ (RED) olacaktır.
 * Implementasyon bir sonraki commit'te (GREEN) eklenecektir.
 *
 * SOLID: Constructor Injection zorunlu (@Autowired field injection yasak).
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisSessionService redisSessionService;

    // PasswordEncoder Spring Security'den gelir; interface olarak tanımlandı
    private final PasswordEncoderPort passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           JwtTokenProvider jwtTokenProvider,
                           RedisSessionService redisSessionService,
                           PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisSessionService = redisSessionService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * ⚠️ RED: Henüz implemente edilmedi.
     * Bu metot UnsupportedOperationException fırlatır → register testleri BAŞARISIZ olacak.
     */
    @Override
    public UserDTO register(RegisterRequest request) {
        // TODO: implement in GREEN phase
        throw new UnsupportedOperationException("register() henüz implemente edilmedi — RED aşaması");
    }

    /**
     * ⚠️ RED: Henüz implemente edilmedi.
     * Bu metot UnsupportedOperationException fırlatır → login testleri BAŞARISIZ olacak.
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // TODO: implement in GREEN phase
        throw new UnsupportedOperationException("login() henüz implemente edilmedi — RED aşaması");
    }

    /**
     * Spring Security PasswordEncoder'ı sarmalar.
     * Test sırasında Mockito ile mock'lanabilir.
     */
    public interface PasswordEncoderPort {
        String encode(CharSequence rawPassword);
        boolean matches(CharSequence rawPassword, String encodedPassword);
    }
}
