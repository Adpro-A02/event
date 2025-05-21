package id.ac.ui.cs.advprog.event.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.event.dto.CreateEventDTO;
import id.ac.ui.cs.advprog.event.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.event.model.EventBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    @Autowired
    private EventRepository eventRepository;


    @Override
    public Event createEvent(CreateEventDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }


        Event event = new EventBuilder()
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setEventDate(dto.getEventDate())
                .setLocation(dto.getLocation())
                .setBasePrice(dto.getBasePrice())
                .setUserId(dto.getUserId())
                .build();


        return eventRepository.save(event);
    }


    @Override
    public UpdateEventDTO updateEvent(UUID id, UpdateEventDTO dto) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        logger.debug("{}",event.getDescription());
        if (event.getStatus() == EventStatus.PUBLISHED) {

            throw new IllegalStateException("Event yang sudah published tidak bisa diubah.");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (dto.getLocation() == null || dto.getLocation().isBlank()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
        if (dto.getEventDate() == null) {
            throw new IllegalArgumentException("Event date is required.");
        }
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setBasePrice(dto.getBasePrice());

        eventRepository.save(event);
        return dto;
    }

    @Override
    public boolean validateEvent(UpdateEventDTO event) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsBeforeEvent = event.getEventDate().minusMonths(3);
        if (event.getStatus() != null && event.getStatus() != EventStatus.PUBLISHED){
            return true;
        }
        else if (event.getStatus() == EventStatus.PUBLISHED && now.isAfter(threeMonthsBeforeEvent)){
            return true;
        }
        return false;
    }

    @Override
    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> getEventByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        return eventRepository.findByEventDate(startOfDay);
    }

    @Override
    public List<Event> listEvents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        UUID userId = UUID.fromString(auth.getPrincipal().toString());

        if ("Organizer".equalsIgnoreCase(role)) {
            return eventRepository.findOwnOrPublishedEvents(userId, EventStatus.PUBLISHED);
        } else {
            return eventRepository.findByStatus(EventStatus.PUBLISHED);
        }
    }

    @Override
    public ResponseDTO<EventStatus> cancelEvent(UUID id) {
        return changeStatus(id, EventStatus.CANCELLED);
    }

    @Override
    public ResponseDTO<EventStatus> publishEvent(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDate = event.getEventDate();


        if (eventDate.isBefore(now)) {
            return new ResponseDTO<>(false, "Cannot publish event with a past date", null);
        }


        if (eventDate.isBefore(now.plusMonths(3))) {
            return new ResponseDTO<>(false, "Event must be scheduled at least 3 months from now to be published", null);
        }

        return changeStatus(id, EventStatus.PUBLISHED);
    }

    @Override
    public ResponseDTO<EventStatus> completeEvent(UUID id) {
        return changeStatus(id, EventStatus.COMPLETED);
    }


    @Override
    public Event getEvent(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }


    private ResponseDTO<EventStatus> changeStatus(UUID id, EventStatus status) {
        Optional<Event> optionalEvent = eventRepository.findById(id);

        if (optionalEvent.isEmpty()) {
            return ResponseDTO.<EventStatus>builder()
                    .success(false)
                    .message("Event not found")
                    .data(null)  
                    .build();
        }

        Event event = optionalEvent.get();
        event.setStatus(status);
        eventRepository.save(event);

       
        return ResponseDTO.<EventStatus>builder()
                .success(true)
                .message("Event status changed to " + status)
                .data(status)  
                .build();
    }





}
