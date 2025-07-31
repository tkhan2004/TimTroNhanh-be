package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.RoomResponse;
import com.phongtro247backend.entity.Favorite;
import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.repository.FavoriteRepository;
import com.phongtro247backend.repository.RoomRepository;
import com.phongtro247backend.service.FavoriteService;
import com.phongtro247backend.service.UserServices;
import com.phongtro247backend.util.JwtUtil;
import com.phongtro247backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImp implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final RoomRepository roomRepository;

    private final UserServices userServices;

    private final JwtUtil jwtUtil;

    private final SecurityUtil securityUtil;

    @Override
    public void addToFavorites(Long roomId) {

    }

    @Override
    public void removeFromFavorites(Long roomId) {

    }

    @Override
    public boolean isFavorited(Long roomId) {
        return false;
    }

    @Override
    public Page<RoomResponse> getFavoriteRooms(Pageable pageable) {
        return null;
    }

    @Override
    public int getFavoriteCount(Long roomId) {
        return (int) favoriteRepository.countByRoomId(roomId);
    }

    @Override
    public boolean toggleFavorite(Long roomId) {
        User user = securityUtil.getCurrentUser();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + roomId));

        if (favoriteRepository.existsByUserAndRoom(user, room)) {
            favoriteRepository.deleteByUserAndRoom(user, room);
            return false;
        } else {
            favoriteRepository.save(Favorite.builder().user(user).room(room).build());
            return true;
        }
    }
}
