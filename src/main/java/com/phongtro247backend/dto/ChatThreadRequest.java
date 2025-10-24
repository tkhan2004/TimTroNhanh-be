package com.phongtro247backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ChatThreadRequest {

    @NotNull(message = "Room ID không được để trống")
    private Long roomId;
}
