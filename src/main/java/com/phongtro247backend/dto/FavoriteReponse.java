package com.phongtro247backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteReponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private RoomResponse room;
    private LocalDateTime createdAt;
}
