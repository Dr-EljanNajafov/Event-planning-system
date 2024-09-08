package com.github.eventplanningsystem.invitation;

public record InvitationDto(
        Long id,
        Long eventId,
        Long userId,
        InvitationStatus invitationStatus
) {
}
