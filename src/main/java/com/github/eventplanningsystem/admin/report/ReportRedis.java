package com.github.eventplanningsystem.admin.report;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("reports")
@Getter
@Setter
public class ReportRedis implements Serializable {

    @Id
    private Long id;

    private int attendeesCount;
    private int declinedCount;
    private LocalDateTime generatedAt;
}

