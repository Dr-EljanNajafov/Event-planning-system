package com.github.eventplanningsystem.admin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetEventRequest {
    private int start;
    private int count;
}
