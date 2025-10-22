package com.phongtro247backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticOverviewResponse {
    private long totalUsers;
    private long newUsersThisMonth;
    private long totalRooms;
    private long newRoomsThisMonth;
    private long pendingReports;
}