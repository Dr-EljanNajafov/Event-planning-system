package com.github.eventplanningsystem.invitation;


import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.event.EventRepository;
import com.github.eventplanningsystem.user.UserE;
import com.github.eventplanningsystem.user.UserRedis;
import com.github.eventplanningsystem.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final InvitationRedisRepository invitationRedisRepository; // Redis repository
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public InvitationDto invitationInfo(long id) {

        // First check Redis cache
        Optional<InvitationRedis> invitationRedisOptional = invitationRedisRepository.findById(String.valueOf(id));
        if (invitationRedisOptional.isPresent()) {
            InvitationRedis invitationRedis = invitationRedisOptional.get();
            return new InvitationDto(
                    invitationRedis.getId(),
                    invitationRedis.getEventId(),
                    invitationRedis.getUserId(),
                    InvitationStatus.valueOf(invitationRedis.getInvitationStatus())
            );
        }
        
        Invitation invitation = invitation(id);
        return new InvitationDto(
                invitation.getId(),
                invitation.getEvent().getId(),
                invitation.getUser().getId(),
                invitation.getInvitationStatus()
        );
    }

    // Отправка приглашения на мероприятие
    public InvitationDto sendInvitation(Long eventId, Long userId, String username) {
        // Найти событие
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        // Найти пользователя, которого мы хотим пригласить
        UserE user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Проверить, что текущий пользователь является владельцем мероприятия
        UserE currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
        if (!event.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only event owner can send invitations");
        }

        // Создать и сохранить приглашение
        Invitation invitation = Invitation
                .builder()
                .event(event)
                .user(user)
                .invitationStatus(InvitationStatus.PENDING)
                .build();
        invitationRepository.save(invitation);


        // Cache the invitation in Redis
        cacheInvitationInRedis(invitation);

        // Вернуть DTO
        return invitationInfo(invitation.getId());
    }

    // Получение списка приглашений текущего пользователя
    public List<InvitationDto> getMyInvitations(String username) {
        // Найти пользователя по имени
        UserE currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // First check Redis cache
        List<InvitationRedis> invitationRedisList = invitationRedisRepository.findAllByUserId(currentUser.getId());
        if (!invitationRedisList.isEmpty()) {
            return invitationRedisList.stream()
                    .map(invitationRedis -> new InvitationDto(
                            invitationRedis.getId(),
                            invitationRedis.getEventId(),
                            invitationRedis.getUserId(),
                            InvitationStatus.valueOf(invitationRedis.getInvitationStatus())
                    ))
                    .collect(Collectors.toList());
        }

        // Получить все приглашения для данного пользователя
        List<Invitation> invitations = invitationRepository.findAllByUser(currentUser);

        // Cache the invitations in Redis
        invitations.forEach(this::cacheInvitationInRedis);

        // Преобразовать список приглашений в DTO
        return invitations.stream()
                .map(invitation -> new InvitationDto(
                        invitation.getId(),
                        invitation.getEvent().getId(),
                        invitation.getUser().getId(),
                        invitation.getInvitationStatus()))
                .collect(Collectors.toList());
    }

    public Invitation invitation(long id) {
        Optional<Invitation> invitationOptional = invitationRepository.findById(id);
        if (invitationOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Invitation with id %d not found".formatted(id)
            );
        }
        return invitationOptional.get();
    }

    private void cacheInvitationInRedis(Invitation invitation) {
        InvitationRedis invitationRedis = new InvitationRedis();
        invitationRedis.setId(invitation.getId());
        invitationRedis.setEventId(invitation.getEvent().getId());
        invitationRedis.setUserId(invitation.getUser().getId());
        invitationRedis.setInvitationStatus(invitation.getInvitationStatus().name());

        invitationRedisRepository.save(invitationRedis);
    }
}
