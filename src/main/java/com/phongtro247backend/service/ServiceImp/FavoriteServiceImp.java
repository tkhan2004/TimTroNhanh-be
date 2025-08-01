package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.RoomResponse;
import com.phongtro247backend.dto.UserResponse;
import com.phongtro247backend.entity.Favorite;
import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.RoomImage;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.repository.FavoriteRepository;
import com.phongtro247backend.repository.RoomRepository;
import com.phongtro247backend.service.FavoriteService;
import com.phongtro247backend.service.RoomService;
import com.phongtro247backend.service.UserServices;
import com.phongtro247backend.util.JwtUtil;
import com.phongtro247backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImp implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final RoomRepository roomRepository;

    private final UserServices userServices;

    private final JwtUtil jwtUtil;

    private final SecurityUtil securityUtil;

    private final RoomService roomService;


    @Override
    @Transactional
    public void addToFavorites(Long roomId) {
        User user = securityUtil.getCurrentUser();

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException(" Không tìm thấy room"));

        if(favoriteRepository.existsByUserAndRoom(user,room)){
            throw new RuntimeException(" Phòng đã được thêm vào danh sách yêu thích rồi");
        }

        favoriteRepository.save(Favorite.builder().user(user).room(room).build());
    }

    @Override
    @Transactional
    public void removeFromFavorites(Long roomId) {
        User user = securityUtil.getCurrentUser();

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException(" Không tìm thấy room"));

        favoriteRepository.deleteByUserAndRoom(user, room);
    }

    @Override
    public boolean isFavorited(Long roomId) {
        User user = securityUtil.getCurrentUser();
        return favoriteRepository.existsByUserIdAndRoomId(user.getId(), roomId);
    }

    @Override
    public Page<RoomResponse> getFavoriteRooms(Pageable pageable) {
        User user = securityUtil.getCurrentUser();

        // Sử dụng query với JOIN để tránh N+1 problem
        Page<Favorite> favorites = favoriteRepository.findByUser(user, pageable);

        return favorites.map(favorite -> {
            Room room = favorite.getRoom();

            // Chỉ lấy thông tin cần thiết cho danh sách
            return RoomResponse.builder()
                    .id(room.getId()) // ← Quan trọng nhất
                    .title(room.getTitle())
                    .price(room.getPrice())
                    .area(room.getArea())
                    .address(room.getAddress())
                    .city(room.getCity())
                    .status(room.getStatus())
                    .imageUrls(room.getImages() != null ?
                            room.getImages().stream()
                                    .limit(1) // Chỉ lấy 1 ảnh đầu
                                    .map(RoomImage::getImageUrl)
                                    .toList() : List.of())
                    .isFavorited(true)
                    .createdAt(room.getCreatedAt())
                    .build();
        });
    }

    @Override
    @Transactional
    public int getFavoriteCount(Long roomId) {
        return (int) favoriteRepository.countByRoomId(roomId);
    }

    @Override
    @Transactional
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
