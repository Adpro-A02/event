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
       
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateEventDTO> updateEvent(@PathVariable UUID id, @RequestBody UpdateEventDTO dto) {
     
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
      
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Event>> getEventsByDate(){
        
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventStatus> publishEvent(@PathVariable UUID id) {
       
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventStatus> cancelEvent(@PathVariable UUID id) {
      
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<EventStatus> completeEvent(@PathVariable UUID id) {
       
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
      
    }
}
