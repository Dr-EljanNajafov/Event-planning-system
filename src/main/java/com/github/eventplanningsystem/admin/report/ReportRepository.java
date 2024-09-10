package com.github.eventplanningsystem.admin.report;

import com.github.eventplanningsystem.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    void deleteAllByEvent(Event event);
}
