package com.phongtro247backend.controller;

import com.phongtro247backend.dto.AuthResponse;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

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
}
