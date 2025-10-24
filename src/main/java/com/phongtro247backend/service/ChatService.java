package com.phongtro247backend.service;

import com.phongtro247backend.dto.ChatMessageRequest;
import com.phongtro247backend.dto.ChatMessageResponse;
import com.phongtro247backend.dto.ChatThreadRequest;
import com.phongtro247backend.dto.ChatThreadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    ChatThreadResponse findOrCreateChatThread(Long roomId);
    Page<ChatThreadResponse> findChatThreads(Pageable pageable);
    Page<ChatMessageResponse> findChatMessages(Long id, Pageable pageable);

    ChatMessageResponse savedMessage(ChatMessageRequest chatMessageRequest, String senderMail);
}
