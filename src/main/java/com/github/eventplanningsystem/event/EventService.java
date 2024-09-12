package com.github.eventplanningsystem.event;

import com.github.eventplanningsystem.invitation.InvitationRepository;
import com.github.eventplanningsystem.user.UserE;
import com.github.eventplanningsystem.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final EventRedisRepository eventRedisRepository; // Redis repository


    public EventDto eventInfo(long id) {
        // First, check Redis cache
        Optional<EventRedis> eventRedisOptional = eventRedisRepository.findById(String.valueOf(id));
        if (eventRedisOptional.isPresent()) {
            EventRedis eventRedis = eventRedisOptional.get();
            return new EventDto(
                    eventRedis.getId(),
                    eventRedis.getTitle(),
                    eventRedis.getStartDateTime(),
                    eventRedis.getEndDateTime(),
                    eventRedis.getLocation(),
                    eventRedis.getDescription()
            );
        }
        Event event = event(id);
        // Cache the event in Redis
        cacheEventInRedis(event);
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getLocation(),
                event.getDescription()
        );
    }

    public EventDto addNew(EventAddRequest eventAddRequest, String username) {
        // Найти владельца (пользователя) по имени пользователя (username)
        UserE owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));

        Event event = Event
                .builder()
                .title(eventAddRequest.getTitle())
                .startDateTime(eventAddRequest.getStartDateTime())
                .endDateTime(eventAddRequest.getEndDateTime())
                .location(eventAddRequest.getLocation())
                .description(eventAddRequest.getDescription())
                .owner(owner)
                .build();
        eventRepository.save(event);
        // Cache the event in Redis
        cacheEventInRedis(event);
        return eventInfo(event.getId());
    }

    public EventDto update(long id, String username, EventUpdateRequest request) {
        Event event = event(id);

        // Проверяем владельца мероприятия по имени пользователя
        if (!event.getOwner().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only owner of event with id %d can update it".formatted(id)
            );
        }

        updateEvent(request, event);
        event = eventRepository.save(event);
        // Update the cache in Redis
        cacheEventInRedis(event);
        return eventInfo(id);
    }

    private static void updateEvent(EventUpdateRequest eventUpdateRequest, Event event) {
        event.setTitle(eventUpdateRequest.getTitle());
        event.setStartDateTime(eventUpdateRequest.getStartDateTime());
        event.setEndDateTime(eventUpdateRequest.getEndDateTime());
        event.setLocation(eventUpdateRequest.getLocation());
        event.setDescription(eventUpdateRequest.getDescription());
    }

    @Transactional
    public void delete(long eventId, String username) {
        Event event = event(eventId);

        // Найти пользователя по username
        UserE owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));

        // Проверить, что именно владелец пытается удалить событие
        if (!event.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only owner of event with id %d can delete it".formatted(eventId)
            );
        }

        deleteEventWithInvitations(eventId);
        // Invalidate the cache in Redis
        eventRedisRepository.deleteById(String.valueOf(eventId));
    }

    @Transactional
    public void deleteEventWithInvitations(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        // Удалить все связанные приглашения
        invitationRepository.deleteAllByEvent(event);

        // Удалить событие
        eventRepository.delete(event);
    }


    public Event event(long id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Event with id %d not found".formatted(id)
            );
        }
        return eventOptional.get();
    }

    private void cacheEventInRedis(Event event) {
        EventRedis eventRedis = new EventRedis();
        eventRedis.setId(event.getId());
        eventRedis.setTitle(event.getTitle());
        eventRedis.setStartDateTime(event.getStartDateTime());
        eventRedis.setEndDateTime(event.getEndDateTime());
        eventRedis.setLocation(event.getLocation());
        eventRedis.setDescription(event.getDescription());
        eventRedis.setOwnerId(event.getOwner().getId());

        eventRedisRepository.save(eventRedis);
    }
}
