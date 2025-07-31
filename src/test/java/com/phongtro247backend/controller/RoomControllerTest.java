package com.phongtro247backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phongtro247backend.util.JwtUtil;
import com.phongtro247backend.dto.RoomRequest;
import com.phongtro247backend.dto.RoomResponse;
import com.phongtro247backend.entity.enums.RoomStatus;
import com.phongtro247backend.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private RoomRequest roomRequest;
    private RoomResponse roomResponse;

    @BeforeEach
    void setUp() {
        // Setup room request
        roomRequest = new RoomRequest();
        roomRequest.setTitle("Phòng trọ cao cấp");
        roomRequest.setDescription("Phòng trọ đầy đủ tiện nghi");
        roomRequest.setPrice(new BigDecimal("3000000"));
        roomRequest.setArea(new BigDecimal("25"));
        roomRequest.setAddress("123 Đường ABC");
        roomRequest.setCity("Hồ Chí Minh");
        roomRequest.setDistrict("Quận 1");
        roomRequest.setWard("Phường Bến Nghé");
        roomRequest.setStatus(RoomStatus.AVAILABLE);

        // Setup room response
        roomResponse = new RoomResponse();
        roomResponse.setId(1L);
        roomResponse.setTitle("Phòng trọ cao cấp");
        roomResponse.setDescription("Phòng trọ đầy đủ tiện nghi");
        roomResponse.setPrice(new BigDecimal("3000000"));
        roomResponse.setArea(new BigDecimal("25"));
        roomResponse.setAddress("123 Đường ABC");
        roomResponse.setCity("Hồ Chí Minh");
        roomResponse.setDistrict("Quận 1");
        roomResponse.setWard("Phường Bến Nghé");
        roomResponse.setStatus(RoomStatus.AVAILABLE);
        roomResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void testCreateRoom_Success() throws Exception {
        // Given
        String token = "valid.jwt.token";
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(token);
        when(roomService.createRoom(eq(token), any(RoomRequest.class))).thenReturn(roomResponse);

        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Tạo phòng thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Phòng trọ cao cấp"));
    }

    @Test
    @WithMockUser(roles = "RENTER")
    void testCreateRoom_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllRooms_Success() throws Exception {
        // Given
        List<RoomResponse> roomList = Arrays.asList(roomResponse);
        Page<RoomResponse> roomPage = new PageImpl<>(roomList);
        
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(null);
        when(roomService.getAllRooms(any(), isNull())).thenReturn(roomPage);

        // When & Then
        mockMvc.perform(get("/api/rooms")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách phòng thành công"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    void testGetRoomById_Success() throws Exception {
        // Given
        Long roomId = 1L;
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(null);
        when(roomService.getRoomById(eq(roomId), isNull())).thenReturn(roomResponse);

        // When & Then
        mockMvc.perform(get("/api/rooms/{id}", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy thông tin phòng thành công"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Phòng trọ cao cấp"));
    }

    @Test
    void testGetRoomById_NotFound() throws Exception {
        // Given
        Long roomId = 999L;
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(null);
        when(roomService.getRoomById(eq(roomId), isNull()))
                .thenThrow(new RuntimeException("Không tìm thấy phòng với ID: " + roomId));

        // When & Then
        mockMvc.perform(get("/api/rooms/{id}", roomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void testUpdateRoom_Success() throws Exception {
        // Given
        Long roomId = 1L;
        String token = "valid.jwt.token";
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(token);
        when(roomService.updateRoom(eq(token), eq(roomId), any(RoomRequest.class)))
                .thenReturn(roomResponse);

        // When & Then
        mockMvc.perform(put("/api/rooms/{id}", roomId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Cập nhật phòng thành công"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void testDeleteRoom_Success() throws Exception {
        // Given
        Long roomId = 1L;
        String token = "valid.jwt.token";
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(token);

        // When & Then
        mockMvc.perform(delete("/api/rooms/{id}", roomId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Xóa phòng thành công"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void testGetMyRooms_Success() throws Exception {
        // Given
        String token = "valid.jwt.token";
        List<RoomResponse> roomList = Arrays.asList(roomResponse);
        Page<RoomResponse> roomPage = new PageImpl<>(roomList);
        
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(token);
        when(roomService.getMyRooms(eq(token), any())).thenReturn(roomPage);

        // When & Then
        mockMvc.perform(get("/api/rooms/my-rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách phòng của tôi thành công"));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void testToggleRoomStatus_Success() throws Exception {
        // Given
        Long roomId = 1L;
        String token = "valid.jwt.token";
        roomResponse.setStatus(RoomStatus.RENTED);
        
        when(jwtUtil.extractTokenFromRequest(any(jakarta.servlet.http.HttpServletRequest.class))).thenReturn(token);
        when(roomService.toggleRoomStatus(eq(token), eq(roomId))).thenReturn(roomResponse);

        // When & Then
        mockMvc.perform(patch("/api/rooms/{id}/toggle-status", roomId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Thay đổi trạng thái phòng thành công"))
                .andExpect(jsonPath("$.data.status").value("RENTED"));
    }
}
