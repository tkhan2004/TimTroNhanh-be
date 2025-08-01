package com.phongtro247backend.dto;

import com.phongtro247backend.entity.enums.RoomStatus;
import com.phongtro247backend.entity.enums.RoomType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoomRequest {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;
    
    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;
    
    @NotNull(message = "Giá phòng không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phòng phải lớn hơn 0")
    @Digits(integer = 8, fraction = 2, message = "Giá phòng không hợp lệ")
    private BigDecimal price;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Diện tích phải lớn hơn 0")
    @Digits(integer = 3, fraction = 2, message = "Diện tích không hợp lệ")
    private BigDecimal area;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    private String address;
    
    @Size(max = 100, message = "Tên thành phố không được vượt quá 100 ký tự")
    private String city;
    
    @Size(max = 100, message = "Tên quận/huyện không được vượt quá 100 ký tự")
    private String district;
    
    @Size(max = 100, message = "Tên phường/xã không được vượt quá 100 ký tự")
    private String ward;
    
    @DecimalMin(value = "-90.0", message = "Vĩ độ phải từ -90 đến 90")
    @DecimalMax(value = "90.0", message = "Vĩ độ phải từ -90 đến 90")
    private Double latitude;
    
    @DecimalMin(value = "-180.0", message = "Kinh độ phải từ -180 đến 180")
    @DecimalMax(value = "180.0", message = "Kinh độ phải từ -180 đến 180")
    private Double longitude;
    
    private RoomStatus status;

    private RoomType roomType;

    // Danh sách ID của các tiện ích
    private List<Long> utilityIds;

    // Danh sách URL hình ảnh
    private List<String> imageUrls;
}
