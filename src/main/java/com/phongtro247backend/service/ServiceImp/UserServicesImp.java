package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.UserRequest;
import com.phongtro247backend.dto.UserResponse;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.entity.enums.UserRole;
import com.phongtro247backend.repository.UserRepository;
import com.phongtro247backend.service.AuthService;
import com.phongtro247backend.service.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServicesImp implements UserServices {

    private final UserRepository userRepository;
    private final AuthService authService;


    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserResponse getCurrentUserProfile(String token) {
        return Optional.ofNullable(token)
                .map(authService::validateToken)
                .map(this::convertToUserResponse)
                .orElseThrow(() -> new RuntimeException("Không thể lấy thông tin người dùng"));
    }

    @Override
    public UserResponse updateCurrentUserProfile(String token, UserRequest request) {
        User currentUser = authService.validateToken(token);
        request.setId(currentUser.getId()); // Đảm bảo user chỉ update chính mình

        return updateUser(currentUser, request);
    }

    @Override
    public UserResponse updateUserById(String token, Long userId, UserRequest request) {
        User currentUser = authService.validateToken(token);

        // Chỉ ADMIN mới được update user khác
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Không có quyền cập nhật thông tin người dùng này");
        }

        request.setId(userId);
        return updateUser(currentUser, request);
    }

    @Override
    public UserResponse getUserProfileById(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToUserResponse)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }

    // Private method để xử lý update logic
    private UserResponse updateUser(User currentUser, UserRequest request) {
        return Optional.ofNullable(currentUser)
                .filter(user -> canUpdateUser(user, request))
                .map(user -> updateUserFields(user, request))
                .map(userRepository::save)
                .map(this::convertToUserResponse)
                .orElseThrow(() -> new RuntimeException("Không thể cập nhật thông tin người dùng"));
    }

    private boolean canUpdateUser(User currentUser, UserRequest request) {
        // Kiểm tra quyền cập nhật
        // User chỉ có thể cập nhật thông tin của chính mình
        // Hoặc ADMIN có thể cập nhật thông tin của bất kỳ ai
        return currentUser.getId().equals(request.getId()) ||
               currentUser.getRole() == UserRole.ADMIN;
    }

    private User updateUserFields(User user, UserRequest request) {
        // Chỉ cập nhật các field không null và không rỗng
        Optional.ofNullable(request.getFullName())
                .filter(name -> !name.trim().isEmpty())
                .ifPresent(user::setFullName);

        Optional.ofNullable(request.getPhone())
                .filter(phone -> !phone.trim().isEmpty())
                .ifPresent(user::setPhone);

        Optional.ofNullable(request.getAvatarUrl())
                .ifPresent(user::setAvatarUrl);

        // Email không được phép thay đổi sau khi tạo tài khoản
        // Role chỉ ADMIN mới được thay đổi
        Optional.ofNullable(request.getRole())
                .filter(role -> user.getRole() == UserRole.ADMIN)
                .ifPresent(user::setRole);

        return user;
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .build();
    }

}
