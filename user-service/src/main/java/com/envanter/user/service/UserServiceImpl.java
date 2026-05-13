package com.envanter.user.service;

import com.envanter.user.dto.LoginRequest;
import com.envanter.user.dto.LoginResponse;
import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;
import com.envanter.user.exception.ConflictException;
import com.envanter.user.exception.UnauthorizedException;
import com.envanter.user.model.Role;
import com.envanter.user.model.User;
import com.envanter.user.repository.UserRepository;
import com.envanter.user.security.JwtTokenProvider;
import com.envanter.user.security.RedisSessionService;
import com.envanter.user.util.UserMapper;
import com.envanter.user.util.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * UserService implementasyonu — SRP refactor sonrası GREEN asama.
 *
 * <p>Bu sınıfın TEK sorumluluğu: iş akışını orkestre etmek.</p>
 * <ul>
 *   <li>Doğrulama  — {@link UserValidator} (ayrı sınıf)</li>
 *   <li>Mapping    — {@link UserMapper}    (ayrı sınıf)</li>
 *   <li>JWT token  — {@link JwtTokenProvider} (ayrı sınıf)</li>
 *   <li>Oturum     — {@link RedisSessionService} (ayrı sınıf)</li>
 * </ul>
 *
 * SOLID: Constructor Injection zorunlu — @Autowired field injection yasak.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository       userRepository;
    private final JwtTokenProvider     jwtTokenProvider;
    private final RedisSessionService  redisSessionService;
    private final UserValidator        userValidator;
    private final UserMapper           userMapper;
    private final PasswordEncoderPort  passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           JwtTokenProvider jwtTokenProvider,
                           RedisSessionService redisSessionService,
                           UserValidator userValidator,
                           UserMapper userMapper,
                           PasswordEncoderPort passwordEncoder) {
        this.userRepository       = userRepository;
        this.jwtTokenProvider     = jwtTokenProvider;
        this.redisSessionService  = redisSessionService;
        this.userValidator        = userValidator;
        this.userMapper           = userMapper;
        this.passwordEncoder      = passwordEncoder;
    }


    // -------------------------------------------------------------------------
    // UserService impl
    // -------------------------------------------------------------------------

    @Override
    public UserDTO register(RegisterRequest request) {
        // 1) Validasyon — UserValidator'a delege edildi (SRP)
        userValidator.validateRegisterRequest(request);

        // 2) Çakışma kontrolü (Username/Email)
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException(
                    "Bu kullanıcı adı zaten kullanılıyor: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(
                    "Bu e-posta zaten kayıtlı: " + request.getEmail());
        }

        // 3) Şifre hashleme
        String hashed = passwordEncoder.encode(request.getPassword());

        // 4) Mapping — UserMapper'a delege edildi (SRP)
        User user = userMapper.toEntity(request, hashed);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 5) Kaydet
        User saved = userRepository.save(user);
        log.info("Yeni kullanıcı kaydedildi: id={}, username={}", saved.getId(), saved.getUsername());

        return userMapper.toDTO(saved);
    }

    @Override
    public UserDTO login(LoginRequest request) {
        // 1) Kullanıcıyı bul
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException(
                        "Kullanıcı adı veya şifre hatalı."));

        // 2) Aktif mi?
        if (!user.isActive()) {
            throw new UnauthorizedException("Hesap devre dışı bırakılmış.");
        }

        // 3) Şifre doğrulama
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Kullanıcı adı veya şifre hatalı.");
        }

        // 4) JWT token üretimi
        String token = jwtTokenProvider.generateToken(user);

        // 5) Redis'e oturum kaydet
        redisSessionService.createSession(token, user.getId(), Duration.ofHours(24));

        log.info("Kullanıcı giriş yaptı: username={}", user.getUsername());

        UserDTO dto = userMapper.toDTO(user);
        dto.setToken(token);
        return dto;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    // generateTokenForUser metodu kaldırıldı çünkü jwtTokenProvider zaten model.User alıyor.

    // -------------------------------------------------------------------------
    // PasswordEncoderPort (iç arayüz — Spring Security'yi sarmalar)
    // -------------------------------------------------------------------------

    /**
     * Spring Security PasswordEncoder'ı sarmalar.
     * Test sırasında Mockito ile mock'lanabilir.
     */
    public interface PasswordEncoderPort {
        String encode(CharSequence rawPassword);
        boolean matches(CharSequence rawPassword, String encodedPassword);
    }
}
