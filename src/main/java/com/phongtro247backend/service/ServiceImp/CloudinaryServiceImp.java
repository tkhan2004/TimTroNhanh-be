package com.phongtro247backend.service.ServiceImp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.phongtro247backend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImp implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 1. Validate file
        validateFile(file);

        // 2. Upload to cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.emptyMap()
        );

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        // 1. Validate file
        validateFile(file);

        // 2. Upload with avatar-specific settings
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder", "phongtro247/avatars",
                "transformation", ObjectUtils.asMap(
                    "width", 200,
                    "height", 200,
                    "crop", "fill",
                    "gravity", "face",
                    "quality", "auto",
                    "format", "jpg"
                )
            )
        );

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public String uploadRoomImage(MultipartFile file) throws IOException {
        // 1. Validate file
        validateFile(file);

        // 2. Upload with room image-specific settings
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder", "phongtro247/rooms",
                "transformation", ObjectUtils.asMap(
                    "width", 800,
                    "height", 600,
                    "crop", "fill",
                    "quality", "auto",
                    "format", "jpg"
                )
            )
        );

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public List<String> uploadRoomImages(List<MultipartFile> files) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String imageUrl = uploadRoomImage(file);
                imageUrls.add(imageUrl);
            }
        }

        return imageUrls;
    }

    @Override
    public boolean deleteFile(String publicId) throws IOException {
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            throw new IOException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public String extractPublicId(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            return null;
        }

        try {
            // Extract public_id from Cloudinary URL
            // Example: https://res.cloudinary.com/demo/image/upload/v1234567890/sample.jpg
            // Public ID: sample

            String[] parts = cloudinaryUrl.split("/");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                // Remove file extension
                int dotIndex = lastPart.lastIndexOf('.');
                if (dotIndex > 0) {
                    return lastPart.substring(0, dotIndex);
                }
                return lastPart;
            }
        } catch (Exception e) {
            // Return null if cannot extract
        }

        return null;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void validateFile(MultipartFile file) throws IOException {
        // 1. Check if file is empty
        if (file == null || file.isEmpty()) {
            throw new IOException("File không được để trống");
        }

        // 2. Check file size (max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IOException("File quá lớn. Kích thước tối đa là 10MB");
        }

        // 3. Check file type
        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            throw new IOException("Chỉ chấp nhận file ảnh (JPG, PNG, GIF, WEBP)");
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }
}
