package com.phongtro247backend.service;

import com.phongtro247backend.dto.ReportRequest;
import com.phongtro247backend.dto.ReportResponse;
import com.phongtro247backend.entity.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface ReportService{

    ReportResponse createReport(ReportRequest reportRequest);

    Page<ReportResponse> getReports(ReportStatus status, Pageable pageable);

    ReportResponse updateReport(Long reportId, ReportStatus status);
}
