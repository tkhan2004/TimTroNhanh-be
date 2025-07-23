package com.phongtro247backend.service;

import com.phongtro247backend.dto.RoomUtilityResponse;

import java.util.List;

public interface RoomUtilityService {
    
    // Get all utilities
    List<RoomUtilityResponse> getAllUtilities();
    
    // Get utility by ID
    RoomUtilityResponse getUtilityById(Long id);
    
    // Create new utility (Admin only)
    RoomUtilityResponse createUtility(String name);
    
    // Update utility (Admin only)
    RoomUtilityResponse updateUtility(Long id, String name);
    
    // Delete utility (Admin only)
    void deleteUtility(Long id);
}
