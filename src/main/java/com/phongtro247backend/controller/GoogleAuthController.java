package com.phongtro247backend.controller;

import com.phongtro247backend.dto.AuthResponse;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.entity.enums.UserRole;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.repository.UserRepository;
import com.phongtro247backend.service.GoogleAuthService;
import com.phongtro247backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @GetMapping("/login")
    public ResponseEntity<String> login(){
        return ResponseEntity.ok(googleAuthService.getGoogleLoginUrl());
    }


    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<AuthResponse>> callback(@RequestParam String code){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(200, "Đăng nhập thành công",googleAuthService.handleGoogleCallBack(code)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, "Đăng nhập thất bại", null));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginWithGoogle(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            // verifyGoogleToken đã xử lý việc tìm/tạo và lưu người dùng
            User user = googleAuthService.verifyGoogleToken(idToken);

            // tạo JWT của hệ thống của bạn
            String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(200, "Đăng nhập thành công", jwt));

        } catch (Exception e) {
            String msg = e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, "Đăng nhập thất bại", msg));
        }
    }
}
