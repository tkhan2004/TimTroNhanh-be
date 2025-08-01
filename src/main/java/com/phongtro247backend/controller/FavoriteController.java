package com.phongtro247backend.controller;

import com.cloudinary.Api;
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

    @GetMapping("/my-favorites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getFavoriteRooms(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            Page<RoomResponse> favoriteRooms = favoriteService.getFavoriteRooms(pageable);

            return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy danh sách phòng yêu thích thành công", favoriteRooms)
            );
        }catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> addToFavorites(
            @PathVariable Long roomId) {
        try {
            favoriteService.addToFavorites(roomId);
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Thêm vào yêu thích thành công", null)
            );
        }catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> removeFromFavorites(
            @PathVariable Long roomId){
        try {
            favoriteService.removeFromFavorites(roomId);
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Xóa khỏi yêu thích thành công", null)
            );
        }catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }


}
