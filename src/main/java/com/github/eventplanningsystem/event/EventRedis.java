package com.github.eventplanningsystem.event;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("events")
@Getter
@Setter
public class EventRedis implements Serializable {

    @Id
    private long id;

    private String title;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String location;

    private String description;

    private long ownerId;
}
