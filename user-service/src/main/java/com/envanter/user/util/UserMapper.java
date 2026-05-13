package com.envanter.user.util;

import com.envanter.user.dto.RegisterRequest;
import com.envanter.user.dto.UserDTO;
import com.envanter.user.model.Role;
import com.envanter.user.model.User;
import org.springframework.stereotype.Component;

/**
 * SRP — Tek Sorumluluk: User entity ↔ DTO dönüşümleri.
 *
 * <p>Mapping mantığı UserServiceImpl'den tamamen ayrıştırıldı.
 * Yeni bir alan eklendiğinde yalnızca bu sınıf değişir.</p>
 */
@Component
public class UserMapper {

    /**
     * RegisterRequest → User entity dönüşümü.
     * passwordHash dışarıdan (şifrelenmiş hali) verilir.
     */
    public User toEntity(RegisterRequest request, String passwordHash) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHash);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.PERSONEL);           // varsayılan rol
        user.setActive(true);
        return user;
    }

    /**
     * User entity → UserDTO dönüşümü.
     * token alanı isteğe bağlı (login sonrası doldurulur).
     */
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        return dto;
    }

    /**
     * User entity → UserDTO (JWT token ile birlikte).
     * Login sonrası kullanılır.
     */
    public UserDTO toDTOWithToken(User user, String token) {
        UserDTO dto = toDTO(user);
        dto.setToken(token);
        return dto;
    }
}
