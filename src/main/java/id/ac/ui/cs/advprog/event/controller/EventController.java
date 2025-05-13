package id.ac.ui.cs.advprog.event.controller;

import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // Validate required fields
        if (event.getEventDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event date cannot be null");
        }
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event title cannot be null or empty");
        }
        if (event.getLocation() == null || event.getLocation().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event location cannot be null or empty");
        }


        Event createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.listEvents();
        return ResponseEntity.ok(events);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable UUID id) {
        try {
            Event event = eventService.getEvent(id);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Event not found")) {
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("message", "Event not found");
                return ResponseEntity.badRequest().body(e.getMessage());

            }
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateEventDTO> updateEvent(@PathVariable UUID id, @RequestBody UpdateEventDTO dto) {
        try {
            UpdateEventDTO updatedEvent = eventService.updateEvent(id, dto);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    e instanceof IllegalArgumentException ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND,
                    e.getMessage()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Event>> getEventsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Event> events = eventService.getEventByDate(date);
        return ResponseEntity.ok(events);
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventStatus> publishEvent(@PathVariable UUID id) {
        try {
            EventStatus event = eventService.publishEvent(id).getData();
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventStatus> cancelEvent(@PathVariable UUID id) {
        try {
            EventStatus event = eventService.cancelEvent(id).getData();
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<EventStatus> completeEvent(@PathVariable UUID id) {
        try {
            EventStatus event = eventService.completeEvent(id).getData();
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }
}
