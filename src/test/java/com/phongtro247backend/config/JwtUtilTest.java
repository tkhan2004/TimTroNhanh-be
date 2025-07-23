package com.phongtro247backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Set up test values for JWT secret and expiration
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForTestingPurposesOnly123456789");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L); // 24 hours
    }

    @Test
    void testExtractTokenFromRequest_WithValidAuthHeader() {
        // Given
        String validAuthHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        // When
        String actualToken = jwtUtil.extractTokenFromRequest(validAuthHeader);

        // Then
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testExtractTokenFromRequest_WithHttpServletRequest() {
        // Given
        String validAuthHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        when(mockRequest.getHeader("Authorization")).thenReturn(validAuthHeader);

        // When
        String actualToken = jwtUtil.extractTokenFromRequest(mockRequest);

        // Then
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testExtractTokenFromRequest_WithNullAuthHeader() {
        // Given
        String nullAuthHeader = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractTokenFromRequest(nullAuthHeader);
        });
    }

    @Test
    void testExtractTokenFromRequest_WithInvalidAuthHeader() {
        // Given
        String invalidAuthHeader = "InvalidHeader token";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractTokenFromRequest(invalidAuthHeader);
        });
    }

    @Test
    void testExtractTokenFromRequest_WithEmptyAuthHeader() {
        // Given
        String emptyAuthHeader = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractTokenFromRequest(emptyAuthHeader);
        });
    }

    @Test
    void testExtractTokenFromRequest_WithHttpServletRequest_NullHeader() {
        // Given
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractTokenFromRequest(mockRequest);
        });
    }
}
