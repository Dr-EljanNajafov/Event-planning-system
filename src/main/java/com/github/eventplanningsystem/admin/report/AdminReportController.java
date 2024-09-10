package com.github.eventplanningsystem.admin.report;

import com.github.eventplanningsystem.admin.AdminService;
import com.github.eventplanningsystem.admin.event.AdminEventService;
import com.github.eventplanningsystem.admin.event.GetEventRequest;
import com.github.eventplanningsystem.event.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/report")
public class AdminReportController {
    private final AdminReportService adminReportService;
    private final AdminService adminService;

    @Operation(summary = "Получение списка всех report")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public List<Long> reports(HttpServletRequest httpServletRequest, GetReportEventRequest getReportEventRequest) {
        return adminService.checkAdmin(httpServletRequest, username -> adminReportService.reports(getReportEventRequest));
    }

    @Operation(summary = "Получение информации о report по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ReportDto reportInfo(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return adminService.checkAdmin(httpServletRequest, username -> adminReportService.reportInfo(id));
    }
}
