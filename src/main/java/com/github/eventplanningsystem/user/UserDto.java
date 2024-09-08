package com.github.eventplanningsystem.user;

public record UserDto(
        Long id,
        String username,
        Role role,
        java.util.List<com.github.eventplanningsystem.invitation.Invitation> invitations
) {
}
