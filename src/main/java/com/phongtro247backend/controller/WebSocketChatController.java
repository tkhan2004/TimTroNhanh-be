package com.phongtro247backend.controller;

import com.phongtro247backend.dto.ChatMessageRequest;
import com.phongtro247backend.dto.ChatMessageResponse;
import com.phongtro247backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessageRequest, Principal principal) {

        // 1. Lấy email (username) của người gửi từ Principal (đã được xác thực)
        String senderEmail = principal.getName();

        // 2. Lưu tin nhắn vào DB
        ChatMessageResponse savedMessage = chatService.savedMessage(chatMessageRequest, senderEmail);

        // 3. Lấy thông tin người nhận từ message (do service điền vào)
        String receiverEmail = savedMessage.getReceiverEmail(); // Cần thêm trường này vào ChatMessageResponse
        String senderEmailFromResponse = savedMessage.getSender().getEmail(); // Lấy email chuẩn

        // 4. Gửi tin nhắn real-time đến KÊNH CÁ NHÂN của cả 2 người

        // 4a. Gửi cho người nhận
        // Spring sẽ tự động chuyển đổi thành "/user/{receiverEmail}/queue/messages"
        messagingTemplate.convertAndSendToUser(
                receiverEmail,
                "/queue/messages",
                savedMessage
        );

        // 4b. Gửi lại cho người gửi (để đồng bộ ID và timestamp)
        messagingTemplate.convertAndSendToUser(
                senderEmailFromResponse,
                "/queue/messages",
                savedMessage
        );
    }
}
