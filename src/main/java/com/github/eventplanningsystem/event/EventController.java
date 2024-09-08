package com.github.eventplanningsystem.event;

import com.github.eventplanningsystem.auth.jwt.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;
    private final JWTService jwtService;

    @Operation(summary = "Добавление нового event")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public EventDto addNew(@RequestBody EventAddRequest eventAddRequest, HttpServletRequest servletRequest) {
        return jwtService.accessUser(servletRequest, username -> eventService.addNew(eventAddRequest, username));
    }
    @Operation(summary = "Получение информации о event по id")
    @GetMapping("/{id}")
    public EventDto eventInfo(@PathVariable long id) {
        return eventService.eventInfo(id);
    }

    @Operation(summary = "Изменение event по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public EventDto update(@PathVariable long id, @RequestBody EventUpdateRequest eventUpdateRequest, HttpServletRequest httpServletRequest) {
        return jwtService.accessUser(httpServletRequest, userId -> eventService.update(id, userId, eventUpdateRequest));
    }

    @Operation(summary = "Удаление event по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, HttpServletRequest servletRequest){
        jwtService.accessUserVoid(servletRequest, username -> eventService.delete(id, username));
    }

}
