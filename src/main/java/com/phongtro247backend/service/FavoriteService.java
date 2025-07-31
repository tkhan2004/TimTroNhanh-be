package com.phongtro247backend.service;

import com.phongtro247backend.dto.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    // Add room to favorites
    void addToFavorites(Long roomId);

    // Remove room from favorites
    void removeFromFavorites(Long roomId);

    // Check if room is favorited by current user
    boolean isFavorited(Long roomId);

    // Get user's favorite rooms with pagination
    Page<RoomResponse> getFavoriteRooms(Pageable pageable);

    // Get favorite count for a room
    int getFavoriteCount(Long roomId);

    // Toggle favorite status
    boolean toggleFavorite(Long roomId);
}
