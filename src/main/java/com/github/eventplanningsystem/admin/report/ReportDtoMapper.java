package com.github.eventplanningsystem.admin.report;

import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.event.EventDto;

import java.util.function.Function;

public class ReportDtoMapper implements Function<Report, ReportDto> {

    @Override
    public ReportDto apply(Report report) {
        return new ReportDto(
                report.getId(),
                report.getAttendeesCount(),
                report.getDeclinedCount(),
                report.getGeneratedAt()
        );
    }
}
