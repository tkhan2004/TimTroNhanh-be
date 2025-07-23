package com.phongtro247backend.repository;

import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long> {
    
    // Tìm images theo room
    List<RoomImage> findByRoom(Room room);
    
    // Tìm images theo room ID
    List<RoomImage> findByRoomId(Long roomId);
    
    // Xóa images theo room
    void deleteByRoom(Room room);
    
    // Xóa images theo room ID
    void deleteByRoomId(Long roomId);
    
    // Đếm số lượng images của room
    long countByRoom(Room room);
    
    // Đếm số lượng images theo room ID
    long countByRoomId(Long roomId);
}
