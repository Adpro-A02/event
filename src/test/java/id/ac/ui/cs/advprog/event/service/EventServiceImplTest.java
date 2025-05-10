package id.ac.ui.cs.advprog.event.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.repository.EventRepository;
import id.ac.ui.cs.advprog.event.service.EventServiceImpl;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;
   

    private Event testEvent;
    private UUID eventId;
    private LocalDateTime eventDate;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusMonths(4);
        
        testEvent = new Event();
        testEvent.setId(eventId);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setEventDate(eventDate);
        testEvent.setLocation("Test Location");
        testEvent.setBasePrice(100.0);
        testEvent.setStatus(EventStatus.DRAFT);
    }

    @Test
    void testCreateEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        
        Event result = eventService.createEvent(testEvent);

        assertNotNull(result);
        assertEquals(testEvent.getTitle(), result.getTitle());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void testUpdateEvent_Success() {
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setEventDate(eventDate);
        updateDTO.setLocation("Updated Location");
        updateDTO.setBasePrice(200.0);
        updateDTO.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        UpdateEventDTO result = eventService.updateEvent(eventId, updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.getTitle(), result.getTitle());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_NotFound() {
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setTitle("Updated Title");
        
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            eventService.updateEvent(eventId, updateDTO);
        });
        
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_InvalidEvent() {
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setEventDate(eventDate);
        updateDTO.setLocation("Updated Location");
        updateDTO.setBasePrice(200.0);
        updateDTO.setStatus(EventStatus.PUBLISHED);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateEvent(eventId, updateDTO);
        });
        
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testValidateEvent_DraftStatus() {
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setEventDate(eventDate);
        updateDTO.setStatus(EventStatus.DRAFT);

        boolean result = eventService.validateEvent(updateDTO);

        assertTrue(result);
    }

    @Test
    void testValidateEvent_PublishedBeforeThreeMonths() {
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setEventDate(LocalDateTime.now().plusMonths(2)); // Less than 3 months
        updateDTO.setStatus(EventStatus.PUBLISHED);

        boolean result = eventService.validateEvent(updateDTO);

        assertTrue(result);
    }

    @Test
    void testValidateEvent_PublishedAfterThreeMonths() {
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setEventDate(LocalDateTime.now().plusMonths(4)); // More than 3 months
        updateDTO.setStatus(EventStatus.PUBLISHED);

        boolean result = eventService.validateEvent(updateDTO);

        assertFalse(result);
    }

    @Test
    void testDeleteEvent() {
        doNothing().when(eventRepository).deleteById(eventId);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    void testGetEventByDate() {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findByEventDate(startOfDay)).thenReturn(expectedEvents);

        List<Event> result = eventService.getEventByDate(date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository, times(1)).findByEventDate(startOfDay);
    }

    @Test
    void testListEvents() {
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findAll()).thenReturn(expectedEvents);

        List<Event> result = eventService.listEvents();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testPublishEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        Event result = eventService.publishEvent(eventId);

        assertNotNull(result);
        assertEquals(EventStatus.PUBLISHED, result.getStatus());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCancelEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        Event result = eventService.cancelEvent(eventId);

        assertNotNull(result);
        assertEquals(EventStatus.CANCELLED, result.getStatus());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCompleteEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        Event result = eventService.completeEvent(eventId);

        assertNotNull(result);
        assertEquals(EventStatus.COMPLETED, result.getStatus());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testGetEvent_Found() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        Optional<Event> result = eventService.getEvent(eventId);

        assertTrue(result.isPresent());
        assertEquals(testEvent, result.get());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void testGetEvent_NotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        Optional<Event> result = eventService.getEvent(eventId);

        assertFalse(result.isPresent());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void testChangeStatus_EventNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            eventService.publishEvent(eventId);
        });
        
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
    }
     @Test
    void testGetUpcomingEvents() {
        // Arrange
        List<Event> upcomingEvents = Arrays.asList(testEvent);
        when(eventRepository.findByEventDateAfter(any(LocalDateTime.class))).thenReturn(upcomingEvents);
        
        // Act
        List<Event> result = eventService.getUpcomingEvents();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        verify(eventRepository, times(1)).findByEventDateAfter(any(LocalDateTime.class));
    }
    
    @Test
    void testGetUpcomingEvents_NoUpcomingEvents() {
        // Arrange
        List<Event> emptyList = Arrays.asList();
        when(eventRepository.findByEventDateAfter(any(LocalDateTime.class))).thenReturn(emptyList);
        
        // Act
        List<Event> result = eventService.getUpcomingEvents();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(eventRepository, times(1)).findByEventDateAfter(any(LocalDateTime.class));
    }
    
    @Test
    void testGetUpcomingEvents_MultipleEvents() {
        // Arrange
        Event anotherUpcomingEvent = new Event();
        anotherUpcomingEvent.setId(UUID.randomUUID());
        anotherUpcomingEvent.setTitle("Another Event");
        anotherUpcomingEvent.setEventDate(LocalDateTime.now().plusMonths(2));
        
        List<Event> upcomingEvents = Arrays.asList(testEvent, anotherUpcomingEvent);
        when(eventRepository.findByEventDateAfter(any(LocalDateTime.class))).thenReturn(upcomingEvents);
        
        // Act
        List<Event> result = eventService.getUpcomingEvents();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findByEventDateAfter(any(LocalDateTime.class));
    }
}