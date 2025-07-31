package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.*;
import com.phongtro247backend.entity.*;
import com.phongtro247backend.entity.enums.RoomStatus;
import com.phongtro247backend.entity.enums.UserRole;
import com.phongtro247backend.repository.*;
import com.phongtro247backend.service.RoomService;
import com.phongtro247backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImp implements RoomService {

    private final RoomRepository roomRepository;

    private final RoomUtilityRepository roomUtilityRepository;

    private final RoomImageRepository roomImageRepository;

    private final RoomPostUtilityRepository roomPostUtilityRepository;

    private final SecurityUtil securityUtil;
    
    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        // 1. Get current user from security context
        User currentUser = securityUtil.getCurrentUser();
        if (currentUser.getRole() != UserRole.OWNER) {
            throw new RuntimeException("Chỉ chủ nhà mới có thể đăng tin cho thuê phòng");
        }

        // 2. Create room
        Room room = new Room();
        room.setOwner(currentUser);
        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());
        room.setPrice(request.getPrice());
        room.setArea(request.getArea());
        room.setAddress(request.getAddress());
        room.setCity(request.getCity());
        room.setDistrict(request.getDistrict());
        room.setWard(request.getWard());
        room.setLatitude(request.getLatitude());
        room.setLongitude(request.getLongitude());

        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        room = roomRepository.save(room);

        // 3. Add utilities if provided
        if (request.getUtilityIds() != null && !request.getUtilityIds().isEmpty()) {
            List<RoomUtility> utilities = roomUtilityRepository.findByIdIn(request.getUtilityIds());
            if (utilities.size() != request.getUtilityIds().size()) {
                throw new RuntimeException("Một số tiện ích không tồn tại");
            }

            for (RoomUtility utility : utilities) {
                RoomPostUtility roomPostUtility = new RoomPostUtility();
                roomPostUtility.setRoom(room);
                roomPostUtility.setUtility(utility);
                roomPostUtilityRepository.save(roomPostUtility);
            }
        }

        // 4. Add images if provided
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    RoomImage roomImage = new RoomImage();
                    roomImage.setRoom(room);
                    roomImage.setImageUrl(imageUrl.trim());
                    roomImageRepository.save(roomImage);
                }
            }
        }

        return buildRoomResponse(room, currentUser);
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        // 1. Find room
        Room room = roomRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        // 2. Get current user (authenticated user)
        User currentUser = securityUtil.getCurrentUser();

        return buildRoomResponse(room, currentUser);
    }

    @Override
    public RoomResponse getRoomByIdForGuest(Long id) {
        // 1. Find room
        Room room = roomRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        // 2. No current user for guest
        return buildRoomResponse(room, null);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        // 1. Get current user and find room
        User currentUser = securityUtil.getCurrentUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        // 2. Check permission
        if (!room.getOwner().getId().equals(currentUser.getId()) &&
            currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Bạn không có quyền cập nhật phòng này");
        }

        // 3. Update room fields
        if (request.getTitle() != null) {
            room.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            room.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            room.setPrice(request.getPrice());
        }
        if (request.getArea() != null) {
            room.setArea(request.getArea());
        }
        if (request.getAddress() != null) {
            room.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            room.setCity(request.getCity());
        }
        if (request.getDistrict() != null) {
            room.setDistrict(request.getDistrict());
        }
        if (request.getWard() != null) {
            room.setWard(request.getWard());
        }
        if (request.getLatitude() != null) {
            room.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            room.setLongitude(request.getLongitude());
        }
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }

        room = roomRepository.save(room);

        // 4. Update utilities if provided
        if (request.getUtilityIds() != null) {
            // Delete old utilities
            roomPostUtilityRepository.deleteByRoom(room);

            // Add new utilities
            if (!request.getUtilityIds().isEmpty()) {
                List<RoomUtility> utilities = roomUtilityRepository.findByIdIn(request.getUtilityIds());
                if (utilities.size() != request.getUtilityIds().size()) {
                    throw new RuntimeException("Một số tiện ích không tồn tại");
                }

                for (RoomUtility utility : utilities) {
                    RoomPostUtility roomPostUtility = new RoomPostUtility();
                    roomPostUtility.setRoom(room);
                    roomPostUtility.setUtility(utility);
                    roomPostUtilityRepository.save(roomPostUtility);
                }
            }
        }

        // 5. Update images if provided
        if (request.getImageUrls() != null) {
            // Delete old images
            roomImageRepository.deleteByRoom(room);

            // Add new images
            if (!request.getImageUrls().isEmpty()) {
                for (String imageUrl : request.getImageUrls()) {
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        RoomImage roomImage = new RoomImage();
                        roomImage.setRoom(room);
                        roomImage.setImageUrl(imageUrl.trim());
                        roomImageRepository.save(roomImage);
                    }
                }
            }
        }

        return buildRoomResponse(room, currentUser);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        if (!room.getOwner().getId().equals(currentUser.getId()) &&
            currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Bạn không có quyền xóa phòng này");
        }

        roomRepository.delete(room);
    }

    @Override
    public Page<RoomResponse> getAllRooms(RoomFilterRequest filterRequest) {
        // 1. Create pagination
        int page = (filterRequest.getPage() != null && filterRequest.getPage() >= 0) ?
                   filterRequest.getPage() : 0;
        int size = (filterRequest.getSize() != null && filterRequest.getSize() > 0 && filterRequest.getSize() <= 100) ?
                   filterRequest.getSize() : 10;
        String sortBy = (filterRequest.getSortBy() != null && !filterRequest.getSortBy().trim().isEmpty()) ?
                        filterRequest.getSortBy() : "createdAt";

        Sort.Direction direction = Sort.Direction.DESC;
        if (filterRequest.getSortDirection() != null && filterRequest.getSortDirection().equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 2. Get current user (authenticated user)
        User currentUser = securityUtil.getCurrentUser();

        // 3. Get rooms based on filters
        Page<Room> rooms;
        if (filterRequest.getUtilityIds() != null && !filterRequest.getUtilityIds().isEmpty()) {
            // Filter by utilities
            rooms = roomRepository.findByUtilityIds(
                filterRequest.getUtilityIds(),
                filterRequest.getUtilityIds().size(),
                pageable
            );
        } else {
            // Use general filters
            rooms = roomRepository.findRoomsWithFilters(
                filterRequest.getKeyword(),
                filterRequest.getCity(),
                filterRequest.getDistrict(),
                filterRequest.getWard(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getMinArea(),
                filterRequest.getMaxArea(),
                filterRequest.getStatus(),
                filterRequest.getOwnerId(),
                pageable
            );
        }

        // 4. Convert to response
        return rooms.map(room -> buildRoomResponse(room, currentUser));
    }

    @Override
    public Page<RoomResponse> getAllRoomsForGuest(RoomFilterRequest filterRequest) {
        // 1. Create pagination
        int page = (filterRequest.getPage() != null && filterRequest.getPage() >= 0) ?
                   filterRequest.getPage() : 0;
        int size = (filterRequest.getSize() != null && filterRequest.getSize() > 0 && filterRequest.getSize() <= 100) ?
                   filterRequest.getSize() : 10;
        String sortBy = (filterRequest.getSortBy() != null && !filterRequest.getSortBy().trim().isEmpty()) ?
                        filterRequest.getSortBy() : "createdAt";

        Sort.Direction direction = Sort.Direction.DESC;
        if (filterRequest.getSortDirection() != null && filterRequest.getSortDirection().equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 2. No current user for guest
        User currentUser = null;

        // 3. Get rooms based on filters
        Page<Room> rooms;
        if (filterRequest.getUtilityIds() != null && !filterRequest.getUtilityIds().isEmpty()) {
            // Filter by utilities
            rooms = roomRepository.findByUtilityIds(
                filterRequest.getUtilityIds(),
                filterRequest.getUtilityIds().size(),
                pageable
            );
        } else {
            // Use general filters
            rooms = roomRepository.findRoomsWithFilters(
                filterRequest.getKeyword(),
                filterRequest.getCity(),
                filterRequest.getDistrict(),
                filterRequest.getWard(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice(),
                filterRequest.getMinArea(),
                filterRequest.getMaxArea(),
                filterRequest.getStatus(),
                filterRequest.getOwnerId(),
                pageable
            );
        }

        // 4. Convert to response
        final User finalCurrentUser = currentUser;
        return rooms.map(room -> buildRoomResponse(room, finalCurrentUser));
    }

    @Override
    public Page<RoomResponse> getMyRooms(RoomFilterRequest filterRequest) {
        // 1. Get current user
        User currentUser = securityUtil.getCurrentUser();
        if (currentUser.getRole() != UserRole.OWNER) {
            throw new RuntimeException("Chỉ chủ nhà mới có thể xem danh sách phòng của mình");
        }

        // 2. Create pagination
        int page = (filterRequest.getPage() != null && filterRequest.getPage() >= 0) ?
                   filterRequest.getPage() : 0;
        int size = (filterRequest.getSize() != null && filterRequest.getSize() > 0 && filterRequest.getSize() <= 100) ?
                   filterRequest.getSize() : 10;
        String sortBy = (filterRequest.getSortBy() != null && !filterRequest.getSortBy().trim().isEmpty()) ?
                        filterRequest.getSortBy() : "createdAt";

        Sort.Direction direction = Sort.Direction.DESC;
        if (filterRequest.getSortDirection() != null && filterRequest.getSortDirection().equalsIgnoreCase("asc")) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // 3. Get rooms and convert to response
        Page<Room> rooms = roomRepository.findByOwner(currentUser, pageable);
        return rooms.map(room -> buildRoomResponse(room, currentUser));
    }

    @Override
    @Transactional
    public RoomResponse toggleRoomStatus(Long id) {
        // 1. Get current user and find room
        User currentUser = securityUtil.getCurrentUser();
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        // 2. Check permission
        if (!room.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền thay đổi trạng thái phòng này");
        }

        // 3. Toggle status
        if (room.getStatus() == RoomStatus.AVAILABLE) {
            room.setStatus(RoomStatus.RENTED);
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        room = roomRepository.save(room);
        return buildRoomResponse(room, currentUser);
    }

    // ==================== PRIVATE HELPER METHOD ====================

    private RoomResponse buildRoomResponse(Room room, User currentUser) {
        // 1. Build owner response
        UserResponse ownerResponse = new UserResponse();
        ownerResponse.setId(room.getOwner().getId());
        ownerResponse.setFullName(room.getOwner().getFullName());
        ownerResponse.setEmail(room.getOwner().getEmail());
        ownerResponse.setPhone(room.getOwner().getPhone());
        ownerResponse.setAvatarUrl(room.getOwner().getAvatarUrl());
        ownerResponse.setRole(room.getOwner().getRole());

        // 2. Get utilities
        List<RoomPostUtility> roomUtilities = roomPostUtilityRepository.findByRoom(room);
        List<RoomUtilityResponse> utilities = new ArrayList<>();
        for (RoomPostUtility ru : roomUtilities) {
            RoomUtilityResponse utilityResponse = new RoomUtilityResponse();
            utilityResponse.setId(ru.getUtility().getId());
            utilityResponse.setName(ru.getUtility().getName());
            utilities.add(utilityResponse);
        }

        // 3. Get images
        List<RoomImage> roomImages = roomImageRepository.findByRoom(room);
        List<String> imageUrls = new ArrayList<>();
        for (RoomImage image : roomImages) {
            imageUrls.add(image.getImageUrl());
        }

        // 4. Check if favorited by current user
        boolean isFavorited = false;
        int favoriteCount = 0;
        if (room.getFavorites() != null) {
            favoriteCount = room.getFavorites().size();
            if (currentUser != null) {
                for (Favorite favorite : room.getFavorites()) {
                    if (favorite.getUser().getId().equals(currentUser.getId())) {
                        isFavorited = true;
                        break;
                    }
                }
            }
        }

        // 5. Build response
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setTitle(room.getTitle());
        response.setDescription(room.getDescription());
        response.setPrice(room.getPrice());
        response.setArea(room.getArea());
        response.setAddress(room.getAddress());
        response.setCity(room.getCity());
        response.setDistrict(room.getDistrict());
        response.setWard(room.getWard());
        response.setLatitude(room.getLatitude());
        response.setLongitude(room.getLongitude());
        response.setStatus(room.getStatus());
        response.setCreatedAt(room.getCreatedAt());
        response.setOwner(ownerResponse);
        response.setUtilities(utilities);
        response.setImageUrls(imageUrls);
        response.setFavoriteCount(favoriteCount);
        response.setIsFavorited(isFavorited);

        return response;
    }
}
