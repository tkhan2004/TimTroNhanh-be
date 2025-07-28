package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.RoomResponse;
import com.phongtro247backend.dto.RoomUtilityResponse;
import com.phongtro247backend.entity.RoomUtility;
import com.phongtro247backend.repository.RoomUtilityRepository;
import com.phongtro247backend.service.RoomUtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomUtilityServiceImp implements RoomUtilityService {

    private final RoomUtilityRepository roomUtilityRepository;

    @Override
    public List<RoomUtilityResponse> getAllUtilities() {

        // 1. Get all utilities from database
        List<RoomUtility> utilities = roomUtilityRepository.findAllByOrderByNameAsc();

        List<RoomUtilityResponse> responses = utilities.stream()
                .map(utiliti -> new RoomUtilityResponse( utiliti.getId(), utiliti.getName()))
                .toList();


        return responses;
    }

    @Override
    @Transactional
    public RoomUtilityResponse createUtility(String name) {
        // 1. Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Tên tiện ích không được để trống");
        }

        // 2. Check if utility already exists
        if (roomUtilityRepository.existsByName(name.trim())) {
            throw new RuntimeException("Tiện ích đã tồn tại: " + name.trim());
        }

        // 3. Create new utility
        RoomUtility utility = new RoomUtility();
        utility.setName(name.trim());
        utility = roomUtilityRepository.save(utility);

        // 4. Convert to response
        RoomUtilityResponse response = new RoomUtilityResponse();
        response.setId(utility.getId());
        response.setName(utility.getName());

        return response;
    }

    @Override
    @Transactional
    public RoomUtilityResponse updateUtility(Long id, String name) {
        // 1. Validate input
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Tên tiện ích không được để trống");
        }

        // 2. Find existing utility
        RoomUtility utility = roomUtilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiện ích với ID: " + id));

        // 3. Check if new name already exists (excluding current utility)
        RoomUtility existingUtility = roomUtilityRepository.findByName(name.trim()).orElse(null);
        if (existingUtility != null && !existingUtility.getId().equals(id)) {
            throw new RuntimeException("Tiện ích đã tồn tại: " + name.trim());
        }

        // 4. Update utility
        utility.setName(name.trim());
        utility = roomUtilityRepository.save(utility);

        // 5. Convert to response
        RoomUtilityResponse response = new RoomUtilityResponse();
        response.setId(utility.getId());
        response.setName(utility.getName());

        return response;
    }

    @Override
    @Transactional
    public void deleteUtility(Long id) {
        // 1. Find existing utility
        RoomUtility utility = roomUtilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiện ích với ID: " + id));

        // 2. Check if utility is being used by any rooms
        // Note: This would require checking RoomPostUtility table
        // For now, we'll allow deletion and let database constraints handle it

        // 3. Delete utility
        roomUtilityRepository.delete(utility);
    }
}
