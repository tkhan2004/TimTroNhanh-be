package com.phongtro247backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessageRequest {

    @NotNull(message = "Thread ID không được để trống")
    private Long threadId;

    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String message;
}
