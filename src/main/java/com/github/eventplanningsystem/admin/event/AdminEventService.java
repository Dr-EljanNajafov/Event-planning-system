package com.github.eventplanningsystem.admin.event;

import com.github.eventplanningsystem.event.Event;
import com.github.eventplanningsystem.event.EventDto;
import com.github.eventplanningsystem.event.EventRepository;
import com.github.eventplanningsystem.event.EventService;
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
        Event event = event(id);
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
        return eventInfo(event.getId());
    }

    public EventDto updateEvent(long id, UpdateEventByAdminRequest request) {
        Event old = eventService.event(id);
        Event event = Event
                .builder()
                .title(request.getTitle())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();
        eventRepository.save(event);
        return eventInfo(event.getId());
    }

    public void deleteEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        invitationRepository.deleteAllByEvent(event);
        eventRepository.delete(eventService.event(id));
    }
}