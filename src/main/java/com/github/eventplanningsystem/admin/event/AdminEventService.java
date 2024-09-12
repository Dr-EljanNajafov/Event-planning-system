package com.github.eventplanningsystem.admin.event;

import com.github.eventplanningsystem.admin.report.ReportRepository;
import com.github.eventplanningsystem.event.*;
import com.github.eventplanningsystem.invitation.InvitationRepository;
import com.github.eventplanningsystem.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminEventService {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final ReportRepository reportRepository;
    private final EventRedisRepository eventRedisRepository; // Redis repository

    public List<Long> events(GetEventRequest request) {
        if (request.getStart()<0 || request.getCount()<0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'start' and 'count' must be > 0"
            );
        }

        List<Event> events = eventRepository.findAll();
        return events
                .subList(
                        Math.min(request.getStart(),
                                events.size()),
                                Math.min(request.getStart() + request.getCount(), events.size()))
                .stream()
                .map(Event::getId)
                .toList();
    }

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

    public EventDto registerEvent(RegisterEventByAdminRequest request) {
        Event event = Event
                .builder()
                .title(request.getTitle())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();
        eventRepository.save(event);
        // Cache the event in Redis
        cacheEventInRedis(event);
        return eventInfo(event.getId());
    }

    public EventDto updateEvent(long id, UpdateEventByAdminRequest request) {
        // Получаем существующее событие по id
        Event existingEvent = eventService.event(id);

        // Обновляем поля существующего события
        existingEvent.setTitle(request.getTitle());
        existingEvent.setStartDateTime(request.getStartDateTime());
        existingEvent.setEndDateTime(request.getEndDateTime());
        existingEvent.setLocation(request.getLocation());
        existingEvent.setDescription(request.getDescription());

        // Сохраняем обновлённое событие
        eventRepository.save(existingEvent);
        // Update the cache in Redis
        cacheEventInRedis(existingEvent);
        // Возвращаем информацию об обновлённом событии
        return eventInfo(existingEvent.getId());
    }


    public void deleteEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        invitationRepository.deleteAllByEvent(event);
        reportRepository.deleteAllByEvent(event);
        eventRepository.delete(eventService.event(id));
        // Remove the event from Redis cache
        eventRedisRepository.deleteById(String.valueOf(id));
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