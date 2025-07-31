package com.phongtro247backend.controller;

import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    /**
     * Upload avatar (authenticated users only)
     * POST /api/upload/avatar
     */
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        try {
            // 1. Upload avatar to Cloudinary
            String imageUrl = cloudinaryService.uploadAvatar(file);
            
            // 2. Prepare response
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Upload avatar thành công", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Upload single room image (OWNER only)
     * POST /api/upload/room-image
     */
    @PostMapping("/room-image")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadRoomImage(
            @RequestParam("file") MultipartFile file) {
        try {
            // 1. Upload room image to Cloudinary
            String imageUrl = cloudinaryService.uploadRoomImage(file);
            
            // 2. Prepare response
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Upload ảnh phòng thành công", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Upload multiple room images (OWNER only)
     * POST /api/upload/room-images
     */
    @PostMapping("/room-images")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadRoomImages(
            @RequestParam("files") List<MultipartFile> files) {
        try {
            // 1. Validate files count
            if (files.size() > 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Chỉ được upload tối đa 10 ảnh", null));
            }
            
            // 2. Upload multiple images to Cloudinary
            List<String> imageUrls = cloudinaryService.uploadRoomImages(files);
            
            // 3. Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("imageUrls", imageUrls);
            response.put("totalFiles", files.size());
            response.put("successCount", imageUrls.size());
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Upload ảnh phòng thành công", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Upload general file (authenticated users)
     * POST /api/upload/file
     */
    @PostMapping("/file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        try {
            // 1. Upload file to Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file);
            
            // 2. Prepare response
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(
                new ApiResponse<>(200, "Upload file thành công", response)
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * Delete file by URL (authenticated users)
     * DELETE /api/upload/file
     */
    @DeleteMapping("/file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @RequestParam("imageUrl") String imageUrl) {
        try {
            // 1. Extract public_id from URL
            String publicId = cloudinaryService.extractPublicId(imageUrl);
            if (publicId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "URL không hợp lệ", null));
            }
            
            // 2. Delete file from Cloudinary
            boolean deleted = cloudinaryService.deleteFile(publicId);
            if (deleted) {
                return ResponseEntity.ok(
                    new ApiResponse<>(200, "Xóa file thành công", null)
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(400, "Không thể xóa file", null));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
