package com.github.eventplanningsystem.admin.report;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;
    private final ReportRedisRepository reportRedisRepository; // Redis repository


    public List<Long> reports(GetReportEventRequest request) {
        if (request.getStart()<0 || request.getCount()<0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'start' and 'count' must be > 0"
            );
        }

        List<Report> reports = reportRepository.findAll();
        return reports
                .subList(
                        Math.min(request.getStart(),
                                reports.size()),
                        Math.min(request.getStart() + request.getCount(), reports.size()))
                .stream()
                .map(Report::getId)
                .toList();
    }

    public ReportDto reportInfo(long id) {
        // First, check Redis cache
        Optional<ReportRedis> reportRedisOptional = reportRedisRepository.findById(String.valueOf(id));
        if (reportRedisOptional.isPresent()) {
            ReportRedis reportRedis = reportRedisOptional.get();
            return new ReportDto(
                    reportRedis.getId(),
                    reportRedis.getAttendeesCount(),
                    reportRedis.getDeclinedCount(),
                    reportRedis.getGeneratedAt()
            );
        }

        Report report = report(id);
        // Cache the report in Redis
        cacheReportInRedis(report);
        return new ReportDto(
                report.getId(),
                report.getAttendeesCount(),
                report.getDeclinedCount(),
                report.getGeneratedAt()
        );
    }

    public Report report(long id) {
        Optional<Report> reportOptional = reportRepository.findById(id);
        if (reportOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Report with id %d not found".formatted(id)
            );
        }
        return reportOptional.get();
    }

    private void cacheReportInRedis(Report report) {
        ReportRedis reportRedis = new ReportRedis();
        reportRedis.setId(report.getId());
        reportRedis.setAttendeesCount(report.getAttendeesCount());
        reportRedis.setDeclinedCount(report.getDeclinedCount());
        reportRedis.setGeneratedAt(report.getGeneratedAt());

        reportRedisRepository.save(reportRedis);
    }
}
