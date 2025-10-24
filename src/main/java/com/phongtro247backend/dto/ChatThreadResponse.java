package com.phongtro247backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatThreadResponse {
    private Long id;

    // Trò chuyện với ai?
    private UserResponse otherUser;

    // Trò chuyện về phòng nào?
    private SimpleRoomResponse room;

    // Tin nhắn cuối cùng
    private ChatMessageResponse lastMessage;
}
