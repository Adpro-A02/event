package id.ac.ui.cs.advprog.event.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.event.dto.CreateEventDTO;
import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.exception.EventNotFoundException;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.model.EventBuilder;
import id.ac.ui.cs.advprog.event.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    @Autowired
    private EventRepository eventRepository;


    @Override
    public Event createEvent(CreateEventDTO dto,UUID userId) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }


        Event event = new EventBuilder()
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setEventDate(dto.getEventDate())
                .setLocation(dto.getLocation())
                .setBasePrice(dto.getBasePrice())
                .setUserId(userId)
                .build();


        return eventRepository.save(event);
    }


    @Override
    public UpdateEventDTO updateEvent(UUID id, UpdateEventDTO dto) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        logger.debug("{}",event.getDescription());
        if (event.getStatus() == EventStatus.PUBLISHED) {

            throw new IllegalArgumentException("Published event restriction cannot be updated");
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
    public void deleteEvent(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        if(event.getStatus() == EventStatus.PUBLISHED){
            throw new IllegalArgumentException("Event refuse to be deleted");
        }
        else{
            eventRepository.delete(event);
        }

    }

    @Override
    public List<Event> getEventByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        return eventRepository.findByEventDate(startOfDay);
    }

    @Override
    public List<Event> listEvents(UUID userId) {
        List<EventStatus> statuses = List.of(EventStatus.PUBLISHED, EventStatus.COMPLETED);

        if (userId == null) {
            return eventRepository.findByStatusIn(statuses);
        } else {
            return eventRepository.findOwnOrPublishedEvents(userId, statuses);
        }
    }



    @Override
    public ResponseDTO<EventStatus> cancelEvent(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        return changeStatus(event, EventStatus.CANCELLED);
    }


    @Override
    @Async
    public CompletableFuture<ResponseDTO<EventStatus>> publishEvent(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventDate = event.getEventDate();

        if (eventDate.isBefore(now)) {
            return CompletableFuture.completedFuture(
                    new ResponseDTO<>(false, "Cannot publish event with a past date", null)
            );
        }

        if (eventDate.isBefore(now.plusMonths(3))) {
            return CompletableFuture.completedFuture(
                    new ResponseDTO<>(false, "Event must be scheduled at least 3 months from now to be published", null)
            );
        }

        ResponseDTO<EventStatus> result = changeStatus(event, EventStatus.PUBLISHED);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public ResponseDTO<EventStatus> completeEvent(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        return changeStatus(event, EventStatus.COMPLETED);
    }
    @Override
    public Event getEvent(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found"));
    }
    private ResponseDTO<EventStatus> changeStatus(Event event, EventStatus status) {

        event.setStatus(status);
        eventRepository.save(event);
        return ResponseDTO.<EventStatus>builder()
                .success(true)
                .message("Event status changed to " + status)
                .data(status)
                .build();
    }
    @Override
    public List<Event> listEventsByOrganizer(UUID organizerId) {
        return eventRepository.findByUserId(organizerId);
    } 


}
