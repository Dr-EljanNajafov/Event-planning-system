package com.github.eventplanningsystem.admin.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetReportEventRequest {
    private int start;
    private int count;
}
