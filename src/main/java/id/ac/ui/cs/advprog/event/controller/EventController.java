package id.ac.ui.cs.advprog.event.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import id.ac.ui.cs.advprog.event.exception.EventNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import id.ac.ui.cs.advprog.event.dto.CreateEventDTO;
import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.service.EventService;
import jakarta.persistence.EntityNotFoundException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/events")
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    private EventService eventService;

    @PreAuthorize("hasAuthority('Organizer')")
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventDTO createEventDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


            String userIdStr = authentication.getName();
//            logger.debug("ini user id{}",principal);
            UUID userId;
            try {
                userId = UUID.fromString(userIdStr);
                logger.debug("ini user id{}",userId.toString());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("User ID bukan UUID valid");
            }

//            createEventDTO.setUserId(userId);
            validateCreateEventDTO(createEventDTO);
            Event createdEvent = eventService.createEvent(createEventDTO,userId);

            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new IllegalArgumentException("Gagal membuat event: " + e.getMessage(), e);
        }
    }


    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.listEvents();
        return ResponseEntity.ok(events);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable("id") UUID id) {
        try {
            Event event = eventService.getEvent(id);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {

            throw new EventNotFoundException("Event not found");



        }
    }
    @PreAuthorize("hasAuthority('Organizer')")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateEventDTO> updateEvent(@PathVariable("id") UUID id, @RequestBody UpdateEventDTO dto) {
        try {

            UpdateEventDTO updatedEvent = eventService.updateEvent(id, dto);

            return ResponseEntity.ok(updatedEvent);
        } catch (EntityNotFoundException e) {
            throw new EventNotFoundException("Event not found");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gagal membuat event: " + e.getMessage(), e);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") UUID id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            throw new EventNotFoundException("Event not found");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Event refuse to delete");
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Event>> getEventsByDate(@PathVariable("date") LocalDate date) {
        List<Event> events = eventService.getEventByDate(date);
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasAuthority('Organizer')")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventStatus> publishEvent(@PathVariable("id") UUID id) {
        try {
            ResponseDTO<EventStatus> response = eventService.publishEvent(id);
            if (!response.isSuccess()) {

                throw new IllegalArgumentException("Event not published");
            }
            return ResponseEntity.ok(response.getData());

        } catch (EventNotFoundException e) {

            throw new EventNotFoundException("Event not found");
        }
    }

    @PreAuthorize("hasAuthority('Organizer')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventStatus> cancelEvent(@PathVariable("id") UUID id) {
        try {
            EventStatus event = eventService.cancelEvent(id).getData();
            return ResponseEntity.ok(event);
        } catch (EventNotFoundException e) {
            throw new EventNotFoundException("Event not found");
        }
    }
    @PreAuthorize("hasAuthority('Organizer')")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<EventStatus> completeEvent(@PathVariable("id") UUID id) {
        try {
            EventStatus event = eventService.completeEvent(id).getData();
            return ResponseEntity.ok(event);
        } catch (EventNotFoundException e) {
            throw new EventNotFoundException("Event not found");
        }
    }

    private void validateCreateEventDTO(CreateEventDTO dto) {
        if (dto.getEventDate() == null) {
            throw new IllegalArgumentException("Event date cannot be null");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Event title cannot be null or empty");
        }
        if (dto.getLocation() == null || dto.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Event location cannot be null or empty");
        }
    }


}
