package com.github.eventplanningsystem.user;

public record UserDto(
        Long id,
        String username,
        Role role
) {
}
