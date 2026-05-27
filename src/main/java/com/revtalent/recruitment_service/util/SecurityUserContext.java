package com.revtalent.recruitment_service.util;

import com.revtalent.recruitment_service.model.Users;
import com.revtalent.recruitment_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class SecurityUserContext {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public Users resolveCurrentUser(String principal) {
        if (principal == null || principal.isBlank()) {
            throw new RuntimeException("Not authenticated");
        }

        Long userId = extractUserIdFromRequest();
        if (userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        }

        try {
            long id = Long.parseLong(principal);
            return userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found: " + id));
        } catch (NumberFormatException ignored) {
            // principal is username/email
        }

        return userRepository.findByUsername(principal)
                .or(() -> userRepository.findByEmail(principal))
                .orElseThrow(() -> new RuntimeException("User not found: " + principal));
    }

    private Long extractUserIdFromRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        HttpServletRequest request = attrs.getRequest();
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return jwtUtil.extractUserId(auth.substring(7));
        }
        return null;
    }
}
