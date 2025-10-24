package com.phongtro247backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ChatMessageResponse {

    private Long id;
    private Long threadId;
    private String message;
    private LocalDateTime timestamp;

    private UserResponse sender;

    @Builder.Default
    private String receiverEmail = "temp@example.com"; // Thêm trường này
}
