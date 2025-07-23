package com.phongtro247backend.repository;

import com.phongtro247backend.entity.RoomUtility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomUtilityRepository extends JpaRepository<RoomUtility, Long> {
    
    // Tìm utility theo tên
    Optional<RoomUtility> findByName(String name);
    
    // Kiểm tra utility có tồn tại theo tên
    boolean existsByName(String name);
    
    // Tìm utilities theo danh sách IDs
    List<RoomUtility> findByIdIn(List<Long> ids);
    
    // Lấy tất cả utilities sắp xếp theo tên
    List<RoomUtility> findAllByOrderByNameAsc();
}
