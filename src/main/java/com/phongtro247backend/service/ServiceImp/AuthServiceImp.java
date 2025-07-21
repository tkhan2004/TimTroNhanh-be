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

@Service
public class AuthServiceImp implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresIn(86400L) // 24 hours
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Email hoặc mật khẩu không chính xác");
        }
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .avatarUrl(request.getAvatarUrl())
                .role(request.getRole() != null ? request.getRole() : UserRole.RENTER)
                .build();

        userRepository.save(user);

        // TODO: Generate token and return
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

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

    @Override
    public User validateToken(String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate token
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Token không hợp lệ");
            }

            // Extract username from token
            String email = jwtUtil.extractUsername(token);

            // Find user by email
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        } catch (Exception e) {
            throw new RuntimeException("Token không hợp lệ: " + e.getMessage());
        }
    }

    @Override
    public void logout(String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate token first
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Token không hợp lệ");
            }

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
            // Remove "Bearer " prefix if present
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }

            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new RuntimeException("Refresh token không hợp lệ");
            }

            // Extract user information from refresh token
            String email = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            // Generate new access token
            String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(86400L) // 24 hours
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Refresh token thất bại: " + e.getMessage());
        }
    }
}
