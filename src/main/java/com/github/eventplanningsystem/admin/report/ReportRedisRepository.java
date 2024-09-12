package com.github.eventplanningsystem.admin.report;

import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.event.EventRedis;
import org.springframework.data.repository.CrudRepository;

public interface ReportRedisRepository extends CrudRepository<ReportRedis, String> {
}
