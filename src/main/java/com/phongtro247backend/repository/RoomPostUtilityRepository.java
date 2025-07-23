package com.phongtro247backend.repository;

import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.RoomPostUtility;
import com.phongtro247backend.entity.RoomUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomPostUtilityRepository extends JpaRepository<RoomPostUtility, Long> {
    
    // Tìm room-utility mappings theo room
    List<RoomPostUtility> findByRoom(Room room);
    
    // Tìm room-utility mappings theo room ID
    List<RoomPostUtility> findByRoomId(Long roomId);
    
    // Tìm room-utility mappings theo utility
    List<RoomPostUtility> findByUtility(RoomUtility utility);
    
    // Tìm room-utility mappings theo utility ID
    List<RoomPostUtility> findByUtilityId(Long utilityId);
    
    // Xóa mappings theo room
    void deleteByRoom(Room room);
    
    // Xóa mappings theo room ID
    void deleteByRoomId(Long roomId);
    
    // Xóa mappings theo utility
    void deleteByUtility(RoomUtility utility);
    
    // Kiểm tra room có utility không
    boolean existsByRoomAndUtility(Room room, RoomUtility utility);
    
    // Kiểm tra room có utility theo IDs
    boolean existsByRoomIdAndUtilityId(Long roomId, Long utilityId);
}
