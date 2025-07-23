package com.phongtro247backend.dto;

import com.phongtro247backend.entity.enums.RoomStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoomFilterRequest {
    
    // Pagination
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
    
    // Filters
    private String keyword; // Tìm kiếm trong title, description, address
    private String city;
    private String district;
    private String ward;
    
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    private BigDecimal minArea;
    private BigDecimal maxArea;
    
    private RoomStatus status;
    
    // Lọc theo tiện ích
    private List<Long> utilityIds;
    
    // Lọc theo chủ nhà
    private Long ownerId;
}
