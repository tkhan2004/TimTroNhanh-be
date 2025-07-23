package com.phongtro247backend.service;

import com.phongtro247backend.dto.RoomFilterRequest;
import com.phongtro247backend.dto.RoomRequest;
import com.phongtro247backend.dto.RoomResponse;
import org.springframework.data.domain.Page;

public interface RoomService {

    // CRUD operations
    RoomResponse createRoom(String token, RoomRequest request);
    RoomResponse getRoomById(Long id, String token);
    RoomResponse updateRoom(String token, Long id, RoomRequest request);
    void deleteRoom(String token, Long id);

    // Search and filter
    Page<RoomResponse> getAllRooms(RoomFilterRequest filterRequest, String token);
    Page<RoomResponse> getMyRooms(String token, RoomFilterRequest filterRequest);

    // Utility methods
    RoomResponse toggleRoomStatus(String token, Long id);
}
