package id.ac.ui.cs.advprog.event.service;

import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.model.Event;
import java.time.LocalDate;
import java.util.Optional;

import java.util.List;
import java.util.UUID;
public interface  EventService {
    UpdateEventDTO updateEvent(UUID id, UpdateEventDTO updateEventDTO);
    void deleteEvent(UUID id);
    List<Event> getEventByDate(LocalDate date);
    List<Event> listEvents();
    Event createEvent(Event event);
    Event publishEvent(UUID id);
    Event cancelEvent(UUID id);
    Event completeEvent(UUID id);
    Optional<Event> getEvent(UUID id);
    boolean validateEvent(UpdateEventDTO event);
    List<Event> getUpcomingEvents(); 
   



    
    
}
