package com.phongtro247backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportRequest {
    @NotNull(message = "Id Room không được để trống")
    private Long roomId;

    @NotNull(message = "Lý do không được để trống")
    private String reason;
}
