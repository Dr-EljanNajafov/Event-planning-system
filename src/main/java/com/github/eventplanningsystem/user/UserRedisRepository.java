package com.github.eventplanningsystem.user;

import com.github.eventplanningsystem.user.UserRedis;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface UserRedisRepository extends CrudRepository<UserRedis, Long> {
    Optional<UserRedis> findByUsername(String username);
}

