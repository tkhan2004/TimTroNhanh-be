package com.phongtro247backend.controller;

import com.phongtro247backend.util.JwtUtil;
import com.phongtro247backend.dto.UserRequest;
import com.phongtro247backend.dto.UserResponse;
import com.phongtro247backend.service.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServices userServices;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private UserController userController;

    private UserRequest userRequest;
    private UserResponse userResponse;
    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = "valid.jwt.token";
        
        userRequest = new UserRequest();
        userRequest.setFullName("Test User");
        userRequest.setPhone("0123456789");
        
        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setFullName("Test User");
        userResponse.setPhone("0123456789");
        userResponse.setEmail("test@example.com");
    }

    @Test
    void testUpdateUserById_Success() {
        // Given
        Long userId = 1L;
        when(jwtUtil.extractTokenFromRequest(httpServletRequest)).thenReturn(validToken);
        when(userServices.updateUserById(validToken, userId, userRequest)).thenReturn(userResponse);

        // When
        ResponseEntity<?> response = userController.updateUserById(userId, userRequest, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(jwtUtil).extractTokenFromRequest(httpServletRequest);
        verify(userServices).updateUserById(validToken, userId, userRequest);
    }

    @Test
    void testUpdateUserById_InvalidToken() {
        // Given
        Long userId = 1L;
        when(jwtUtil.extractTokenFromRequest(httpServletRequest))
                .thenThrow(new IllegalArgumentException("Invalid Authorization header format"));

        // When
        ResponseEntity<?> response = userController.updateUserById(userId, userRequest, httpServletRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(jwtUtil).extractTokenFromRequest(httpServletRequest);
        verify(userServices, never()).updateUserById(anyString(), anyLong(), any(UserRequest.class));
    }

    @Test
    void testUpdateCurrentUserProfile_Success() {
        // Given
        when(jwtUtil.extractTokenFromRequest(httpServletRequest)).thenReturn(validToken);
        when(userServices.updateCurrentUserProfile(validToken, userRequest)).thenReturn(userResponse);

        // When
        ResponseEntity<?> response = userController.updateCurrentUserProfile(userRequest, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(jwtUtil).extractTokenFromRequest(httpServletRequest);
        verify(userServices).updateCurrentUserProfile(validToken, userRequest);
    }

    @Test
    void testGetCurrentUserProfile_Success() {
        // Given
        when(jwtUtil.extractTokenFromRequest(httpServletRequest)).thenReturn(validToken);
        when(userServices.getCurrentUserProfile(validToken)).thenReturn(userResponse);

        // When
        ResponseEntity<?> response = userController.getCurrentUserProfile(httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(jwtUtil).extractTokenFromRequest(httpServletRequest);
        verify(userServices).getCurrentUserProfile(validToken);
    }
}
