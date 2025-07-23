package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.config.JwtUtil;
import com.phongtro247backend.dto.AuthResponse;
import com.phongtro247backend.dto.LoginRequest;
import com.phongtro247backend.dto.RegisterRequest;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.entity.enums.UserRole;
import com.phongtro247backend.repository.UserRepository;
import com.phongtro247backend.service.AuthService;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // Helper methods using Java 8 functional programming
    private String extractTokenFromBearer(String token) {
        return Optional.ofNullable(token)
                .filter(t -> t.startsWith("Bearer "))
                .map(t -> t.substring(7))
                .orElse(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }

    private AuthResponse buildAuthResponse(User user, String accessToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(86400L) // 24 hours
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    private String generateTokenForUser(User user) {
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return Optional.of(request)
                .map(this::authenticateUser)
                .map(LoginRequest::getEmail)
                .map(this::findUserByEmail)
                .map(user -> buildAuthResponse(user, generateTokenForUser(user)))
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không chính xác"));
    }

    private LoginRequest authenticateUser(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            return request;
        } catch (Exception e) {
            throw new RuntimeException("Email hoặc mật khẩu không chính xác");
        }
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        return Optional.of(request)
                .filter(this::isEmailAvailable)
                .map(this::createUserFromRequest)
                .map(userRepository::save)
                .map(user -> buildAuthResponse(user, generateTokenForUser(user)))
                .orElseThrow(() -> new RuntimeException("Email đã tồn tại"));
    }

    private boolean isEmailAvailable(RegisterRequest request) {
        return !userRepository.existsByEmail(request.getEmail());
    }

    private User createUserFromRequest(RegisterRequest request) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .avatarUrl(request.getAvatarUrl())
                .role(Optional.ofNullable(request.getRole()).orElse(UserRole.RENTER))
                .build();
    }

    @Override
    public User validateToken(String token) {
        try {
            String cleanToken = extractTokenFromBearer(token);

            if (!jwtUtil.validateToken(cleanToken)) {
                throw new RuntimeException("Token không hợp lệ");
            }

            return Optional.of(cleanToken)
                    .map(jwtUtil::extractUsername)
                    .map(this::findUserByEmail)
                    .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        } catch (Exception e) {
            throw new RuntimeException("Token không hợp lệ: " + e.getMessage());
        }
    }

    @Override
    public void logout(String token) {
        try {
            Optional.ofNullable(token)
                    .map(this::extractTokenFromBearer)
                    .filter(jwtUtil::validateToken)
                    .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

            // For stateless JWT, logout is handled on client side by removing token
            // In a production environment, you might want to implement a token blacklist
            // For now, we'll just validate that the token is valid before "logging out"

            // TODO: Implement token blacklist if needed
            // blacklistedTokens.add(token);

        } catch (Exception e) {
            throw new RuntimeException("Logout thất bại: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            return Optional.ofNullable(refreshToken)
                    .map(this::extractTokenFromBearer)
                    .filter(jwtUtil::validateToken)
                    .map(jwtUtil::extractUsername)
                    .map(this::findUserByEmail)
                    .map(user -> buildAuthResponse(user, generateTokenForUser(user)))
                    .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

        } catch (Exception e) {
            throw new RuntimeException("Refresh token thất bại: " + e.getMessage());
        }
    }
}
