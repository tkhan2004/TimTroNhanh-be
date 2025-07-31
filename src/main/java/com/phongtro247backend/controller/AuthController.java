package com.phongtro247backend.controller;

import com.phongtro247backend.dto.AuthResponse;
import com.phongtro247backend.dto.LoginRequest;
import com.phongtro247backend.dto.RegisterRequest;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {

        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(200, "Đăng ký thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>>  login(@Valid @RequestBody LoginRequest request) {

        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(200, "Đăng nhập thành công", response));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Token không hợp lệ", null));
            }

            authService.logout(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(200, "Đăng xuất thành công", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Refresh token không hợp lệ", null));
            }

            AuthResponse response = authService.refreshToken(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(200, "Refresh token thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Token không hợp lệ", null));
            }

            authService.validateToken(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(200, "Token hợp lệ", "Token is valid"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(401, e.getMessage(), null));
        }
    }
}
