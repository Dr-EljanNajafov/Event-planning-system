package com.github.eventplanningsystem.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface EventRedisRepository extends CrudRepository<EventRedis, String> {
    @NonNull
    Optional<Event> findById(@NonNull Long id);
}

