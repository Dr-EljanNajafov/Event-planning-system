package com.github.eventplanningsystem.invitation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("invitations")
@Getter
@Setter
public class InvitationRedis implements Serializable {

    @Id
    private long id;

    // Храните идентификатор события, вместо ссылки на Event-объект
    @Indexed
    private long eventId;

    // Храните идентификатор пользователя, вместо ссылки на UserE-объект
    @Indexed
    private long userId;

    // Статус приглашения
    private String invitationStatus;
}

