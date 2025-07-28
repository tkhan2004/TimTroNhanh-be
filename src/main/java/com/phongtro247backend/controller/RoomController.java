package com.phongtro247backend.controller;

import com.phongtro247backend.config.JwtUtil;
import com.phongtro247backend.dto.RoomFilterRequest;
import com.phongtro247backend.dto.RoomRequest;
import com.phongtro247backend.dto.RoomResponse;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.RoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    @Autowired
    private final RoomService roomService;

    @Autowired
    private final JwtUtil jwtUtil;

    /**
     * Tạo phòng mới (chỉ OWNER)
     * POST /api/rooms
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody RoomRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 1. Extract token from request
            String token = jwtUtil.extractTokenFromRequest(httpRequest);

            // 2. Create room
            RoomResponse createdRoom = roomService.createRoom(token, request);

            // 3. Return success response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Tạo phòng thành công", createdRoom));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Lấy danh sách tất cả phòng (có filter và pagination)
     * GET /api/rooms
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getAllRooms(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String ward,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String minArea,
            @RequestParam(required = false) String maxArea,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerId,
            HttpServletRequest httpRequest) {
        try {
            // 1. Extract token (optional for public access)
            String token = jwtUtil.extractTokenFromRequest(httpRequest);

            // 2. Build filter request
            RoomFilterRequest filterRequest = buildFilterRequest(
                page, size, sortBy, sortDirection, keyword, city, district, ward,
                minPrice, maxPrice, minArea, maxArea, status, ownerId
            );

            // 3. Get rooms
            Page<RoomResponse> rooms = roomService.getAllRooms(filterRequest, token);

            // 4. Return success response
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy danh sách phòng thành công", rooms)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Lấy chi tiết phòng theo ID
     * GET /api/rooms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            // 1. Extract token (optional)
            String token = jwtUtil.extractTokenFromRequest(httpRequest);

            // 2. Get room details
            RoomResponse room = roomService.getRoomById(id, token);

            // 3. Return success response
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy thông tin phòng thành công", room)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    /**
     * Cập nhật thông tin phòng (chỉ owner hoặc admin)
     * PUT /api/rooms/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 1. Extract token from request
            String token = jwtUtil.extractTokenFromRequest(httpRequest);

            // 2. Update room
            RoomResponse updatedRoom = roomService.updateRoom(token, id, request);

            // 3. Return success response
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Cập nhật phòng thành công", updatedRoom)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Xóa phòng (chỉ owner hoặc admin)
     * DELETE /api/rooms/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            // 1. Extract token from request
            String token = jwtUtil.extractTokenFromRequest(httpRequest);

            // 2. Delete room
            roomService.deleteRoom(token, id);

            // 3. Return success response
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Xóa phòng thành công", null)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Lấy danh sách phòng của tôi (chỉ OWNER)
     * GET /api/rooms/my-rooms
     */
    @GetMapping("/my-rooms")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getMyRooms(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest httpRequest) {
        try {
            // 1. Extract token from request
            String token = jwtUtil.extractTokenFromRequest(httpRequest);

            // 2. Build filter request
            RoomFilterRequest filterRequest = new RoomFilterRequest();
            filterRequest.setPage(page);
            filterRequest.setSize(size);
            filterRequest.setSortBy(sortBy);
            filterRequest.setSortDirection(sortDirection);

            // 3. Get my rooms
            Page<RoomResponse> rooms = roomService.getMyRooms(token, filterRequest);

            // 4. Return success response
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy danh sách phòng của tôi thành công", rooms)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Thay đổi trạng thái phòng (AVAILABLE <-> RENTED)
     * PATCH /api/rooms/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<RoomResponse>> toggleRoomStatus(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            // 1. Extract token from request
            String token = jwtUtil.extractTokenFromRequest(httpRequest);

            // 2. Toggle room status
            RoomResponse updatedRoom = roomService.toggleRoomStatus(token, id);

            // 3. Return success response
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Thay đổi trạng thái phòng thành công", updatedRoom)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Build RoomFilterRequest from request parameters
     */
    private RoomFilterRequest buildFilterRequest(
            Integer page, Integer size, String sortBy, String sortDirection,
            String keyword, String city, String district, String ward,
            String minPrice, String maxPrice, String minArea, String maxArea,
            String status, Long ownerId) {

        RoomFilterRequest filterRequest = new RoomFilterRequest();
        filterRequest.setPage(page);
        filterRequest.setSize(size);
        filterRequest.setSortBy(sortBy);
        filterRequest.setSortDirection(sortDirection);
        filterRequest.setKeyword(keyword);
        filterRequest.setCity(city);
        filterRequest.setDistrict(district);
        filterRequest.setWard(ward);
        filterRequest.setOwnerId(ownerId);

        // Parse price range
        if (minPrice != null && !minPrice.trim().isEmpty()) {
            try {
                filterRequest.setMinPrice(new java.math.BigDecimal(minPrice));
            } catch (NumberFormatException e) {
                // Ignore invalid price format
            }
        }

        if (maxPrice != null && !maxPrice.trim().isEmpty()) {
            try {
                filterRequest.setMaxPrice(new java.math.BigDecimal(maxPrice));
            } catch (NumberFormatException e) {
                // Ignore invalid price format
            }
        }

        // Parse area range
        if (minArea != null && !minArea.trim().isEmpty()) {
            try {
                filterRequest.setMinArea(new java.math.BigDecimal(minArea));
            } catch (NumberFormatException e) {
                // Ignore invalid area format
            }
        }

        if (maxArea != null && !maxArea.trim().isEmpty()) {
            try {
                filterRequest.setMaxArea(new java.math.BigDecimal(maxArea));
            } catch (NumberFormatException e) {
                // Ignore invalid area format
            }
        }

        // Parse status
        if (status != null && !status.trim().isEmpty()) {
            try {
                filterRequest.setStatus(com.phongtro247backend.entity.enums.RoomStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore invalid status format
            }
        }

        return filterRequest;
    }
}
