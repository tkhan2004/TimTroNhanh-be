package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.StatisticOverviewResponse;
import com.phongtro247backend.entity.enums.ReportStatus;
import com.phongtro247backend.repository.ReportRepository;
import com.phongtro247backend.repository.RoomRepository;
import com.phongtro247backend.repository.UserRepository;
import com.phongtro247backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final ReportRepository reportRepository;

    @Override
    public StatisticOverviewResponse getStatisticsOverview() {

        // 1. Lấy ngày đầu tiên của tháng hiện tại
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // 2. Lấy thống kê
        long totalUsers = userRepository.count();
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(startOfMonth);

        long totalRooms = roomRepository.count();
        long newRoomsThisMonth = roomRepository.countByCreatedAtAfter(startOfMonth);

        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING); //

        // 3. Xây dựng response
        return StatisticOverviewResponse.builder()
                .totalUsers(totalUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .totalRooms(totalRooms)
                .newRoomsThisMonth(newRoomsThisMonth)
                .pendingReports(pendingReports)
                .build();
    }
}
