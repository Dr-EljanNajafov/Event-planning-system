package com.github.eventplanningsystem.event;

import com.github.eventplanningsystem.user.UserE;
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
public class EventAddRequest {
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
