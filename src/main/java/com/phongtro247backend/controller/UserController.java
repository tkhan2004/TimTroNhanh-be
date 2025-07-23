package com.phongtro247backend.controller;

import com.phongtro247backend.config.JwtUtil;
import com.phongtro247backend.dto.UserRequest;
import com.phongtro247backend.dto.UserResponse;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private  UserServices userServices;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('RENTER', 'OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            UserResponse userResponse = userServices.getCurrentUserProfile(token);

            return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy thông tin người dùng thành công", userResponse)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(401, e.getMessage(), null));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('RENTER', 'OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUserProfile(
            @Valid @RequestBody UserRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            UserResponse updatedUser = userServices.updateCurrentUserProfile(token, request);

            return ResponseEntity.ok(
                new ApiResponse<>(200, "Cập nhật thông tin thành công", updatedUser)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserById(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request,
            HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            UserResponse updatedUser = userServices.updateUserById(token, userId, request);

            return ResponseEntity.ok(
                new ApiResponse<>(200, "Cập nhật thông tin người dùng thành công", updatedUser)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        try {
            UserResponse userResponse = userServices.getUserProfileById(userId);

            return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy thông tin người dùng thành công", userResponse)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Helper method to extract JWT token from HttpServletRequest
     * @param request HttpServletRequest containing Authorization header
     * @return Clean JWT token without "Bearer " prefix
     * @throws IllegalArgumentException if Authorization header is invalid
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        return jwtUtil.extractTokenFromRequest(request);
    }

}
