package com.phongtro247backend.service;

import com.phongtro247backend.dto.RoomFilterRequest;
import com.phongtro247backend.dto.RoomRequest;
import com.phongtro247backend.dto.RoomResponse;
import org.springframework.data.domain.Page;

public interface RoomService {

    // CRUD operations
    RoomResponse createRoom(RoomRequest request);
    RoomResponse getRoomById(Long id);
    RoomResponse getRoomByIdForGuest(Long id); // For non-authenticated users
    RoomResponse updateRoom(Long id, RoomRequest request);
    void deleteRoom(Long id);

    // Search and filter
    Page<RoomResponse> getAllRooms(RoomFilterRequest filterRequest);
    Page<RoomResponse> getAllRoomsForGuest(RoomFilterRequest filterRequest); // For non-authenticated users
    Page<RoomResponse> getMyRooms(RoomFilterRequest filterRequest);

    // Utility methods
    RoomResponse toggleRoomStatus(Long id);
}
