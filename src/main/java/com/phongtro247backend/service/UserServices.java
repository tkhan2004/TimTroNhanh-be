package com.phongtro247backend.service;

import com.phongtro247backend.dto.UserRequest;
import com.phongtro247backend.dto.UserResponse;
import com.phongtro247backend.entity.User;

import java.util.Optional;

public interface UserServices {
    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);

    UserResponse getCurrentUserProfile(String token);

    UserResponse updateCurrentUserProfile(String token, UserRequest request);

    UserResponse updateUserById(String token, Long userId, UserRequest request);

    UserResponse getUserProfileById(Long userId);
}
