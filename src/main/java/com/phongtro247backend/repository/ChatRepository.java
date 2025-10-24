package com.phongtro247backend.repository;

import com.phongtro247backend.entity.ChatMessage;
import com.phongtro247backend.entity.ChatThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatMessage,Long> {
    Optional<ChatMessage> findTopByThreadOrderBySentAtDesc(ChatThread thread);

    Page<ChatMessage> findByThread(ChatThread thread, Pageable pageable);
}

