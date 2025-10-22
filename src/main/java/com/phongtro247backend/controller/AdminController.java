package com.phongtro247backend.controller;

import com.phongtro247backend.dto.ReportResponse;
import com.phongtro247backend.dto.StatisticOverviewResponse;
import com.phongtro247backend.entity.enums.ReportStatus;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.AdminService;
import com.phongtro247backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReportService reportService;

    private final AdminService adminService;

    @Operation(summary = "Lấy thống kê tổng quan cho Dashboard")
    @GetMapping("/statistics/overview")
    public ResponseEntity<ApiResponse<StatisticOverviewResponse>>  statisticsOverview(){

        try {
            StatisticOverviewResponse statisticOverviewResponse = adminService.getStatisticsOverview();
            return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thống kê thành công", statisticOverviewResponse));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(200, "Lấy thống kê thất bại", null));
        }
    }

    @Operation(summary = "Lấy danh sách tố cáo (dành cho admin)")
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> getAllReports(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) ReportStatus status) { // Filter theo status
        try {
            // 1. Tạo Pageable (style từ FavoriteController)
            Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // 2. Gọi service
            Page<ReportResponse> reportsPage = reportService.getReports(status, pageable);

            // 3. Trả về response
            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Lấy danh sách tố cáo thành công", reportsPage)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /**
     * [MỚI] Admin cập nhật trạng thái của một tố cáo (PENDING -> RESOLVED)
     * PUT /api/admin/reports/{id}/status
     */
    @Operation(summary = "Admin cập nhật tráng thái báo cáo")
    @PutMapping("/reports/{id}/status")
    public ResponseEntity<ApiResponse<ReportResponse>> updateReportStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) { // Nhận status từ body
        try {
            // 1. Parse status từ request body
            String statusString = statusUpdate.get("status");
            if (statusString == null) {
                throw new RuntimeException("Trạng thái (status) là bắt buộc");
            }
            ReportStatus status = ReportStatus.valueOf(statusString.toUpperCase()); //

            // 2. Gọi service
            ReportResponse updatedReport = reportService.updateReport(id, status);

            // 3. Trả về response
            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Cập nhật trạng thái tố cáo thành công", updatedReport)
            );

        } catch (IllegalArgumentException e) {
            // Lỗi nếu status gửi lên không phải PENDING hoặc RESOLVED
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "Trạng thái không hợp lệ", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }


}
