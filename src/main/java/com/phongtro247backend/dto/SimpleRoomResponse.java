package com.phongtro247backend.dto;

import com.phongtro247backend.entity.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleRoomResponse {

    private Long id;

    private String title;

    private RoomStatus status;

    private String address;
}
