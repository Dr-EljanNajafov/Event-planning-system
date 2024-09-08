package com.github.eventplanningsystem.event;

import java.util.function.Function;

public class EventDtoMapper implements Function<Event, EventDto> {
    @Override
    public EventDto apply(Event event) {
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getLocation(),
                event.getDescription()
        );
    }
}
