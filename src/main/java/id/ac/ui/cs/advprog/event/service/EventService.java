package id.ac.ui.cs.advprog.event.service;

import id.ac.ui.cs.advprog.event.dto.CreateEventDTO;
import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event;
import java.time.LocalDate;
import java.util.Optional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface  EventService {
    UpdateEventDTO updateEvent(UUID id, UpdateEventDTO updateEventDTO);
    void deleteEvent(UUID id);
    List<Event> getEventByDate(LocalDate date);
    List<Event> listEvents();
    CompletableFuture<Event> createEvent(CreateEventDTO dto, UUID userId);
    ResponseDTO<EventStatus> cancelEvent(UUID id);
    ResponseDTO<EventStatus> publishEvent(UUID id);
    ResponseDTO<EventStatus> completeEvent(UUID id);
    Event getEvent(UUID id);
    boolean validateEvent(UpdateEventDTO event);


   



    
    
}
