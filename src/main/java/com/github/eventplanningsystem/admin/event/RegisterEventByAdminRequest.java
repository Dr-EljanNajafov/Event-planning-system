package com.github.eventplanningsystem.admin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterEventByAdminRequest {

    @NonNull
    private String title;
    @NonNull
    private LocalDateTime startDateTime;
    @NonNull
    private LocalDateTime endDateTime;
    @NonNull
    private String location;
    private String description;
}
