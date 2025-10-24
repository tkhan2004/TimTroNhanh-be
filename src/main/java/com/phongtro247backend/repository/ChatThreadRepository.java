package com.phongtro247backend.repository;

import com.phongtro247backend.entity.ChatMessage;
import com.phongtro247backend.entity.ChatThread;
import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatThreadRepository extends JpaRepository<ChatThread,Long> {
    Optional<ChatThread> findByRoomAndRenter(Room room, User renter);

    Page<ChatThread> findByRenterOrOwner(User renter, User owner, Pageable pageable);
}
