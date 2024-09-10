package com.github.eventplanningsystem.admin.event;

import com.github.eventplanningsystem.admin.AdminService;
import com.github.eventplanningsystem.event.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
public class AdminEventController {
    private final AdminEventService adminEventService;
    private final AdminService adminService;

    @Operation(summary = "Получение списка всех event")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public List<Long> events(HttpServletRequest httpServletRequest, GetEventRequest getEventRequest) {
        return adminService.checkAdmin(httpServletRequest, username -> adminEventService.events(getEventRequest));
    }

    @Operation(summary = "Получение информации о event по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public EventDto eventInfo(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        return adminService.checkAdmin(httpServletRequest, username -> adminEventService.eventInfo(id));
    }

    @Operation(summary = "Создание нового event")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public EventDto registerEvent(
            HttpServletRequest httpServletRequest,
            @RequestBody RegisterEventByAdminRequest registerEventByAdminRequest) {
        return adminService.checkAdmin(httpServletRequest, username -> adminEventService.registerEvent(registerEventByAdminRequest));
    }

    @Operation(summary = "Изменение event по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public EventDto updateEvent(
            HttpServletRequest httpServletRequest,
            @RequestBody UpdateEventByAdminRequest updateEventByAdminRequest,
            @PathVariable Long id
    ) {
        return adminService.checkAdmin(httpServletRequest, username -> adminEventService.updateEvent(id, updateEventByAdminRequest));
    }

    @Operation(summary = "Удаление event по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public void deleteEvent(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        adminService.checkAdminVoid(httpServletRequest, username -> adminEventService.deleteEvent(id));
    }
}
