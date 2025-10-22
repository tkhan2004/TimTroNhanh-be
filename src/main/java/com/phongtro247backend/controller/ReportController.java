package com.phongtro247backend.controller;

import com.phongtro247backend.dto.ReportRequest;
import com.phongtro247backend.dto.ReportResponse;
import com.phongtro247backend.entity.Report;
import com.phongtro247backend.payload.ApiResponse;
import com.phongtro247backend.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Tạo tố cáo mới")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(@Valid @RequestBody ReportRequest reportRequest) {
        try {
            ReportResponse reportResponse = reportService.createReport(reportRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(200, "Gửi tố cáo thành công", reportResponse));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "Gửi tố cáo thất bại ",null));
        }
    }

}
