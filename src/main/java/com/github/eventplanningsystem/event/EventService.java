package com.github.eventplanningsystem.event;

import com.github.eventplanningsystem.user.UserE;
import com.github.eventplanningsystem.user.UserRepository;
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
    public EventDto eventInfo(long id){
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
        eventRepository.save(event);
        return eventInfo(id);
    }

    private static void updateEvent(EventUpdateRequest eventUpdateRequest, Event event) {
        event.setTitle(eventUpdateRequest.getTitle());
        event.setStartDateTime(eventUpdateRequest.getStartDateTime());
        event.setEndDateTime(eventUpdateRequest.getEndDateTime());
        event.setLocation(eventUpdateRequest.getLocation());
        event.setDescription(eventUpdateRequest.getDescription());
    }

    public void delete(long eventId, String username) {
        Event event = event(eventId);

        // Найти пользователя по username
        UserE owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));

        // Проверить, что именно владелец пытается удалить событие
        if (!event.getOwner().equals(owner)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only owner of event with id %d can delete it".formatted(eventId)
            );
        }

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
}
