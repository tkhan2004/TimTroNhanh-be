package com.phongtro247backend.service;

import com.phongtro247backend.dto.RoomRequest;
import com.phongtro247backend.dto.RoomResponse;
import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.RoomUtility;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.entity.enums.RoomStatus;
import com.phongtro247backend.entity.enums.UserRole;
import com.phongtro247backend.repository.*;
import com.phongtro247backend.service.ServiceImp.RoomServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomUtilityRepository roomUtilityRepository;

    @Mock
    private RoomImageRepository roomImageRepository;

    @Mock
    private RoomPostUtilityRepository roomPostUtilityRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private RoomServiceImp roomService;

    private User ownerUser;
    private User renterUser;
    private RoomRequest roomRequest;
    private Room savedRoom;
    private List<RoomUtility> utilities;

    @BeforeEach
    void setUp() {
        // Setup owner user
        ownerUser = User.builder()
                .id(1L)
                .fullName("Nguyen Van A")
                .email("owner@example.com")
                .phone("0123456789")
                .role(UserRole.OWNER)
                .build();

        // Setup renter user
        renterUser = User.builder()
                .id(2L)
                .fullName("Nguyen Van B")
                .email("renter@example.com")
                .phone("0987654321")
                .role(UserRole.RENTER)
                .build();

        // Setup room request
        roomRequest = RoomRequest.builder()
                .title("Phòng trọ cao cấp")
                .description("Phòng trọ đầy đủ tiện nghi, gần trường đại học")
                .price(new BigDecimal("3000000"))
                .area(new BigDecimal("25.5"))
                .address("123 Đường ABC, Phường XYZ")
                .city("Hồ Chí Minh")
                .district("Quận 1")
                .ward("Phường Bến Nghé")
                .latitude(10.762622)
                .longitude(106.660172)
                .status(RoomStatus.AVAILABLE)
                .utilityIds(Arrays.asList(1L, 2L))
                .imageUrls(Arrays.asList("http://example.com/image1.jpg", "http://example.com/image2.jpg"))
                .build();

        // Setup saved room
        savedRoom = Room.builder()
                .id(1L)
                .owner(ownerUser)
                .title(roomRequest.getTitle())
                .description(roomRequest.getDescription())
                .price(roomRequest.getPrice())
                .area(roomRequest.getArea())
                .address(roomRequest.getAddress())
                .city(roomRequest.getCity())
                .district(roomRequest.getDistrict())
                .ward(roomRequest.getWard())
                .latitude(roomRequest.getLatitude())
                .longitude(roomRequest.getLongitude())
                .status(roomRequest.getStatus())
                .createdAt(LocalDateTime.now())
                .build();

        // Setup utilities
        utilities = Arrays.asList(
                RoomUtility.builder().id(1L).name("Wifi").build(),
                RoomUtility.builder().id(2L).name("Điều hòa").build()
        );
    }

    @Test
    void testCreateRoom_Success() {
        // Given
        String validToken = "valid.jwt.token";
        
        when(authService.validateToken(validToken)).thenReturn(ownerUser);
        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);
        when(roomUtilityRepository.findByIdIn(roomRequest.getUtilityIds())).thenReturn(utilities);
        when(roomPostUtilityRepository.findByRoom(savedRoom)).thenReturn(Arrays.asList());
        when(roomImageRepository.findByRoom(savedRoom)).thenReturn(Arrays.asList());

        // When
        RoomResponse result = roomService.createRoom(validToken, roomRequest);

        // Then
        assertNotNull(result);
        assertEquals(savedRoom.getId(), result.getId());
        assertEquals(roomRequest.getTitle(), result.getTitle());
        assertEquals(roomRequest.getDescription(), result.getDescription());
        assertEquals(roomRequest.getPrice(), result.getPrice());
        assertEquals(roomRequest.getArea(), result.getArea());
        assertEquals(roomRequest.getAddress(), result.getAddress());
        assertEquals(roomRequest.getCity(), result.getCity());
        assertEquals(roomRequest.getDistrict(), result.getDistrict());
        assertEquals(roomRequest.getWard(), result.getWard());
        assertEquals(roomRequest.getLatitude(), result.getLatitude());
        assertEquals(roomRequest.getLongitude(), result.getLongitude());
        assertEquals(roomRequest.getStatus(), result.getStatus());
        
        // Verify owner information
        assertNotNull(result.getOwner());
        assertEquals(ownerUser.getId(), result.getOwner().getId());
        assertEquals(ownerUser.getFullName(), result.getOwner().getFullName());
        assertEquals(ownerUser.getEmail(), result.getOwner().getEmail());
        assertEquals(ownerUser.getRole(), result.getOwner().getRole());

        // Verify method calls
        verify(authService).validateToken(validToken);
        verify(roomRepository).save(any(Room.class));
        verify(roomUtilityRepository).findByIdIn(roomRequest.getUtilityIds());
        verify(roomPostUtilityRepository, times(2)).save(any());
        verify(roomImageRepository, times(2)).save(any());
    }

    @Test
    void testCreateRoom_RenterUserShouldFail() {
        // Given
        String validToken = "valid.jwt.token";
        
        when(authService.validateToken(validToken)).thenReturn(renterUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomService.createRoom(validToken, roomRequest);
        });

        assertEquals("Chỉ chủ nhà mới có thể đăng tin cho thuê phòng", exception.getMessage());
        
        // Verify that room was not saved
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testCreateRoom_InvalidTokenShouldFail() {
        // Given
        String invalidToken = "invalid.jwt.token";
        
        when(authService.validateToken(invalidToken))
                .thenThrow(new RuntimeException("Token không hợp lệ"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomService.createRoom(invalidToken, roomRequest);
        });

        assertEquals("Token không hợp lệ", exception.getMessage());
        
        // Verify that room was not saved
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testCreateRoom_WithoutUtilitiesAndImages() {
        // Given
        String validToken = "valid.jwt.token";
        RoomRequest requestWithoutExtras = RoomRequest.builder()
                .title("Phòng trọ đơn giản")
                .description("Phòng trọ cơ bản")
                .price(new BigDecimal("2000000"))
                .area(new BigDecimal("20"))
                .address("456 Đường DEF")
                .city("Hồ Chí Minh")
                .district("Quận 2")
                .ward("Phường An Phú")
                .build();
        
        when(authService.validateToken(validToken)).thenReturn(ownerUser);
        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);
        when(roomPostUtilityRepository.findByRoom(savedRoom)).thenReturn(Arrays.asList());
        when(roomImageRepository.findByRoom(savedRoom)).thenReturn(Arrays.asList());

        // When
        RoomResponse result = roomService.createRoom(validToken, requestWithoutExtras);

        // Then
        assertNotNull(result);
        assertEquals(savedRoom.getId(), result.getId());
        
        // Verify that utility and image methods were not called
        verify(roomUtilityRepository, never()).findByIdIn(any());
        verify(roomPostUtilityRepository, never()).save(any());
        verify(roomImageRepository, never()).save(any());
    }

    @Test
    void testCreateRoom_InvalidUtilitiesShouldFail() {
        // Given
        String validToken = "valid.jwt.token";
        
        when(authService.validateToken(validToken)).thenReturn(ownerUser);
        when(roomUtilityRepository.findByIdIn(roomRequest.getUtilityIds()))
                .thenReturn(Arrays.asList(utilities.get(0))); // Only return 1 utility instead of 2

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomService.createRoom(validToken, roomRequest);
        });

        assertEquals("Một số tiện ích không tồn tại", exception.getMessage());
        
        // Verify that room was saved but utilities failed
        verify(roomRepository).save(any(Room.class));
        verify(roomUtilityRepository).findByIdIn(roomRequest.getUtilityIds());
    }
}
