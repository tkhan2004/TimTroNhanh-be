package com.phongtro247backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {

    // Upload single file
    String uploadFile(MultipartFile file) throws IOException;

    // Upload avatar with specific folder and transformation
    String uploadAvatar(MultipartFile file) throws IOException;

    // Upload room image with specific folder and transformation
    String uploadRoomImage(MultipartFile file) throws IOException;

    // Upload multiple room images
    List<String> uploadRoomImages(List<MultipartFile> files) throws IOException;

    // Delete file by public_id
    boolean deleteFile(String publicId) throws IOException;

    // Extract public_id from Cloudinary URL
    String extractPublicId(String cloudinaryUrl);
}
