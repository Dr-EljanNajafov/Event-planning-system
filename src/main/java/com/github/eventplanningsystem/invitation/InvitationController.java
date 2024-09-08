package com.github.eventplanningsystem.invitation;

import com.github.eventplanningsystem.auth.jwt.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invitation")
public class InvitationController {

    private final InvitationService invitationService;
    private final JWTService jwtService;

    @Operation(summary = "Отправка приглашения на мероприятие")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/send")
    public InvitationDto sendInvitation(@RequestBody InvitationSendRequest invitationSendRequest, HttpServletRequest request) {
        // Используем JWTService для получения текущего пользователя
        return jwtService.accessUser(request, username -> invitationService.sendInvitation(
                invitationSendRequest.getEventId(),
                invitationSendRequest.getUserId(),
                username
        ));
    }

    @Operation(summary = "Получение списка приглашений текущего пользователя")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/myInvitations")
    public List<InvitationDto> getMyInvitations(HttpServletRequest request) {
        // Используем JWTService для получения текущего пользователя
        return jwtService.accessUser(request, invitationService::getMyInvitations);
    }
}
