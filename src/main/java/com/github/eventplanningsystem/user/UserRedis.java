package com.github.eventplanningsystem.user;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("users")
@Getter
@Setter
public class UserRedis implements Serializable {

    @Indexed
    private long id;

    @Indexed
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}

