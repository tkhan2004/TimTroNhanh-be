package com.phongtro247backend.service.ServiceImp;

import com.phongtro247backend.dto.ReportRequest;
import com.phongtro247backend.dto.ReportResponse;
import com.phongtro247backend.dto.SimpleRoomResponse;
import com.phongtro247backend.dto.UserResponse;
import com.phongtro247backend.entity.Report;
import com.phongtro247backend.entity.Room;
import com.phongtro247backend.entity.User;
import com.phongtro247backend.entity.enums.ReportStatus;
import com.phongtro247backend.repository.ReportRepository;
import com.phongtro247backend.repository.RoomRepository;
import com.phongtro247backend.repository.UserRepository;
import com.phongtro247backend.service.ReportService;
import com.phongtro247backend.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;

    private ReportRepository reportRepository;

    private final SecurityUtil securityUtil;

    @Override
    public ReportResponse createReport(ReportRequest reportRequest) {
        User reporter = securityUtil.getCurrentUser();

        Room room = roomRepository.findById(reportRequest.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Không tim thấy phòng"));

        Report report = new Report();
        report.setReporter(reporter);
        report.setRoom(room);
        report.setReason(reportRequest.getReason());
        report.setStatus(ReportStatus.PENDING);

        Report saved = reportRepository.save(report);

        return buildReportResponse(saved);

    }

    @Override
    public Page<ReportResponse> getReports(ReportStatus status, Pageable pageable) {

        Page<Report> reportPage;

        if(status != null) {
            reportPage = reportRepository.findByStatus(status,pageable);
        }else {
            reportPage = reportRepository.findAll(pageable);
        }

        return reportPage.map(this::buildReportResponse);
    }

    @Override
    public ReportResponse updateReport(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(()-> new EntityNotFoundException(" Không tìm thấy đơn tố cáo"));

        report.setStatus(status);
        Report saved = reportRepository.save(report);
        return buildReportResponse(saved);
    }

    // Helper Method
    private ReportResponse buildReportResponse(Report report) {
        // 1. Build Reporter (UserResponse)
        User reporterUser = report.getReporter();
        UserResponse reporterResponse = new UserResponse();
        reporterResponse.setId(reporterUser.getId());
        reporterResponse.setFullName(reporterUser.getFullName());
        reporterResponse.setEmail(reporterUser.getEmail());
        reporterResponse.setPhone(reporterUser.getPhone());
        reporterResponse.setAvatarUrl(reporterUser.getAvatarUrl());
        reporterResponse.setRole(reporterUser.getRole());

        // 2. Build Room (SimpleRoomResponse)
        SimpleRoomResponse roomResponse = null;
        if (report.getRoom() != null) {
            Room room = report.getRoom();
            roomResponse = SimpleRoomResponse.builder()
                    .id(room.getId())
                    .title(room.getTitle())
                    .address(room.getAddress())
                    .build();
        }

        // 3. Build ReportResponse chính
        return ReportResponse.builder()
                .id(report.getId())
                .reason(report.getReason())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .reporter(reporterResponse)
                .room(roomResponse)
                .build();

    }
}



