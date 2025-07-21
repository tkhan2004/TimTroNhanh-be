package com.phongtro247backend.service;

import com.phongtro247backend.dto.AuthResponse;
import com.phongtro247backend.dto.LoginRequest;
import com.phongtro247backend.dto.RegisterRequest;
import com.phongtro247backend.entity.User;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    User validateToken(String token);

    void logout(String token);

    AuthResponse refreshToken(String refeshToken);
}
