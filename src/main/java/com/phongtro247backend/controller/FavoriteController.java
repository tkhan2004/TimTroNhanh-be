package com.phongtro247backend.controller;

import com.phongtro247backend.dto.RoomResponse;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Toggle favorite status for a room
     * POST /api/favorites/{roomId}/toggle
     */
    @PostMapping("/{roomId}/toggle")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFavorite(
            @PathVariable Long roomId) {
        try {
            // 1. Toggle favorite (user from SecurityContext)
            boolean isFavorited = favoriteService.toggleFavorite(roomId);
            
            // 2. Get updated favorite count
            int favoriteCount = favoriteService.getFavoriteCount(roomId);
            
            // 3. Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("isFavorited", isFavorited);
            response.put("favoriteCount", favoriteCount);
            response.put("roomId", roomId);
            
            String message = isFavorited ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích";
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, message, response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
