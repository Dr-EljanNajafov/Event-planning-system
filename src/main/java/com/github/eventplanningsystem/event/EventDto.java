package com.github.eventplanningsystem.event;

import com.github.eventplanningsystem.user.UserE;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        String title,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String location,
        String description
) {
}


