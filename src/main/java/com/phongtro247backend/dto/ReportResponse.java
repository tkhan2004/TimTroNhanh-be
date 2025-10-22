package com.phongtro247backend.dto;

import com.phongtro247backend.entity.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {

    private Long id;
    private String reason;
    private ReportStatus status; //
    private LocalDateTime createdAt;

    // Người tố cáo là ai?
    private UserResponse reporter; //

    // Phòng nào bị tố cáo?
    private SimpleRoomResponse room;
}
