package com.phongtro247backend.repository;

import com.phongtro247backend.entity.Favorite;
import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Tìm favorite theo user
    Page<Favorite> findByUser(User user, Pageable pageable);

    // Tìm favorite theo user và room
    Optional<Favorite> findByUserAndRoom(User user, Room room);

    boolean existsByUserAndRoom(User user, Room room);

    boolean existsByUserIdAndRoomId(Long userId, Long roomId);

    long countByRoomId(Long roomId);

    void deleteByUserAndRoom(User user, Room room);




}
