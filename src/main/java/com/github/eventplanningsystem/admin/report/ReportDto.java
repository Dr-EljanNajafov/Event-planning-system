package com.github.eventplanningsystem.admin.report;

import java.time.LocalDateTime;

public record ReportDto(
        Long id,
        int attendeesCount,
        int declinedCount,
        LocalDateTime generatedAt
) {
}
