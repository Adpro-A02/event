package id.ac.ui.cs.advprog.event.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.repository.EventRepository;

@Service
public class EventServiceimpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    @Override
    public Event createEvent(Event event) {
    
    }
    @Override
    public UpdateEventDTO updateEvent(UpdateEventDTO updateEvent){
        
    }

    @Override
    public List<Event> listEvents() {
        
    }

    @Override
    public Optional<Event> getEvent(UUID id) {
        
    }
    @Override
    public Event publishEvent(UUID id) {
        
    }
    @Override
    public Event cancelEvent(UUID id) {
       
    }

    @Override
    public Event completeEvent(UUID id) {
        
    }

    @Override
    public List<Event> getEventByDate(){

    }

    @Override
    public Optional<Event> getEvent(Long eventId) {
       
    }



}
