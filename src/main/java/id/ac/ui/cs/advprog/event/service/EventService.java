package id.ac.ui.cs.advprog.event.service;

import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.model.Event;

import java.util.List;
import java.util.UUID;
public interface  EventService {
    UpdateEventDTO updateEvent();
    void deleteEvent();
    List<Event> getEventByDate();
    List<Event> listEvents();
    Event createEvent();
    Event publishEvent();
    Event cancelEvent();
    Event completeEvent();
    Optional<Event> getEvent();
    



    
    
}
