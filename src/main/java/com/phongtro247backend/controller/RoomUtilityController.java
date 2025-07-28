package com.phongtro247backend.controller;

import com.phongtro247backend.dto.RoomUtilityResponse;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.RoomUtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilities")
@RequiredArgsConstructor
public class RoomUtilityController {

    private final RoomUtilityService roomUtilityService;

    /**
     * Lấy danh sách tất cả tiện ích (public - không cần auth)
     * GET /api/utilities
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomUtilityResponse>>> getAllUtilities() {
        try {
            List<RoomUtilityResponse> utilities = roomUtilityService.getAllUtilities();
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy danh sách tiện ích thành công", utilities)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Tạo tiện ích mới (chỉ ADMIN)
     * POST /api/utilities
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomUtilityResponse>> createUtility(
            @RequestParam String name) {
        try {
            RoomUtilityResponse utility = roomUtilityService.createUtility(name);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Tạo tiện ích thành công", utility));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Cập nhật tiện ích (chỉ ADMIN)
     * PUT /api/utilities/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomUtilityResponse>> updateUtility(
            @PathVariable Long id,
            @RequestParam String name) {
        try {
            RoomUtilityResponse utility = roomUtilityService.updateUtility(id, name);
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Cập nhật tiện ích thành công", utility)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Xóa tiện ích (chỉ ADMIN)
     * DELETE /api/utilities/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUtility(@PathVariable Long id) {
        try {
            roomUtilityService.deleteUtility(id);
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Xóa tiện ích thành công", null)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
