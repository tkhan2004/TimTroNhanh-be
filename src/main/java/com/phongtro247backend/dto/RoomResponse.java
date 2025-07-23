package com.phongtro247backend.dto;

import com.phongtro247backend.entity.enums.RoomStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoomResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal area;
    private String address;
    private String city;
    private String district;
    private String ward;
    private Double latitude;
    private Double longitude;
    private RoomStatus status;
    private LocalDateTime createdAt;

    // Thông tin chủ nhà
    private UserResponse owner;

    // Danh sách tiện ích
    private List<RoomUtilityResponse> utilities;

    // Danh sách hình ảnh
    private List<String> imageUrls;

    // Thống kê
    private Integer favoriteCount;
    private Boolean isFavorited; // Có được user hiện tại yêu thích không
}
