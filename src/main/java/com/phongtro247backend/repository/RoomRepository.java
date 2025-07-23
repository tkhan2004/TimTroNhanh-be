package com.phongtro247backend.repository;

import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.entity.enums.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    // Tìm rooms theo owner
    Page<Room> findByOwner(User owner, Pageable pageable);
    
    // Tìm rooms theo status
    Page<Room> findByStatus(RoomStatus status, Pageable pageable);
    
    // Tìm rooms theo city
    Page<Room> findByCity(String city, Pageable pageable);
    
    // Tìm rooms theo district
    Page<Room> findByCityAndDistrict(String city, String district, Pageable pageable);
    
    // Tìm rooms theo ward
    Page<Room> findByCityAndDistrictAndWard(String city, String district, String ward, Pageable pageable);
    
    // Tìm rooms theo khoảng giá
    Page<Room> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Tìm rooms theo khoảng diện tích
    Page<Room> findByAreaBetween(BigDecimal minArea, BigDecimal maxArea, Pageable pageable);
    
    // Tìm kiếm full-text
    @Query("SELECT r FROM Room r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Room> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Tìm rooms với nhiều điều kiện
    @Query("SELECT DISTINCT r FROM Room r " +
           "LEFT JOIN r.roomUtilities ru " +
           "LEFT JOIN ru.utility u " +
           "WHERE (:keyword IS NULL OR " +
           "       LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "       LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "       LOWER(r.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:city IS NULL OR r.city = :city) " +
           "AND (:district IS NULL OR r.district = :district) " +
           "AND (:ward IS NULL OR r.ward = :ward) " +
           "AND (:minPrice IS NULL OR r.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR r.price <= :maxPrice) " +
           "AND (:minArea IS NULL OR r.area >= :minArea) " +
           "AND (:maxArea IS NULL OR r.area <= :maxArea) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:ownerId IS NULL OR r.owner.id = :ownerId)")
    Page<Room> findRoomsWithFilters(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("district") String district,
            @Param("ward") String ward,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minArea") BigDecimal minArea,
            @Param("maxArea") BigDecimal maxArea,
            @Param("status") RoomStatus status,
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
    
    // Tìm rooms theo utility IDs
    @Query("SELECT DISTINCT r FROM Room r " +
           "JOIN r.roomUtilities ru " +
           "WHERE ru.utility.id IN :utilityIds " +
           "GROUP BY r.id " +
           "HAVING COUNT(DISTINCT ru.utility.id) = :utilityCount")
    Page<Room> findByUtilityIds(@Param("utilityIds") List<Long> utilityIds, 
                               @Param("utilityCount") long utilityCount, 
                               Pageable pageable);
    
    // Đếm số lượng rooms theo owner
    long countByOwner(User owner);
    
    // Đếm số lượng rooms theo status
    long countByStatus(RoomStatus status);
    
    // Lấy room với eager loading
    @Query("SELECT r FROM Room r " +
           "LEFT JOIN FETCH r.images " +
           "LEFT JOIN FETCH r.roomUtilities ru " +
           "LEFT JOIN FETCH ru.utility " +
           "LEFT JOIN FETCH r.owner " +
           "WHERE r.id = :id")
    Optional<Room> findByIdWithDetails(@Param("id") Long id);
}
