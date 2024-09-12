package com.github.eventplanningsystem.invitation;

import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.user.UserRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InvitationRedisRepository extends CrudRepository<InvitationRedis, String> {

    // Метод поиска приглашений по пользователю в Redis
    List<InvitationRedis> findAllByUserId(Long userId);

    // Удаление всех приглашений, связанных с событием
    void deleteAllByEventId(Long eventId);
}