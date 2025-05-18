package id.ac.ui.cs.advprog.event.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.event.dto.CreateEventDTO;
import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.model.EventBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
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

    private ArgumentCaptor<Event> eventCaptor;
    private Event testEvent;
    private CreateEventDTO validEventDTO;
    private Event savedEvent;
    private UUID eventId;
    private LocalDateTime eventDate;
    private UUID userId;

    private UUID userId1;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusMonths(4);
        userId = UUID.randomUUID();
        userId1 = UUID.randomUUID();

        testEvent = new Event();
        testEvent.setId(eventId);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setEventDate(eventDate);
        testEvent.setLocation("Test Location");
        testEvent.setBasePrice(100.0);
        testEvent.setStatus(EventStatus.DRAFT);
        testEvent.setUserId(userId);

        validEventDTO = new CreateEventDTO();
        validEventDTO.setTitle("Test Event");
        validEventDTO.setDescription("Test Description");
        validEventDTO.setEventDate(eventDate);
        validEventDTO.setLocation("Test Location");
        validEventDTO.setBasePrice(100.0);
        validEventDTO.setUserId(userId1);


        savedEvent = new Event();
        savedEvent.setId(UUID.randomUUID()); // Repository would generate this
        savedEvent.setTitle(validEventDTO.getTitle());
        savedEvent.setDescription(validEventDTO.getDescription());
        savedEvent.setEventDate(validEventDTO.getEventDate());
        savedEvent.setLocation(validEventDTO.getLocation());
        savedEvent.setBasePrice(validEventDTO.getBasePrice());
        savedEvent.setStatus(EventStatus.DRAFT); // Default status
        savedEvent.setUserId(validEventDTO.getUserId());
    }

    @Test
    void testCreateEvent() {
        // Arrange
        CreateEventDTO dto = new CreateEventDTO();
        dto.setTitle("My Event");
        dto.setDescription("Description");
        dto.setEventDate(LocalDateTime.of(2025, 5, 18, 10, 0));
        dto.setLocation("Jakarta");
        dto.setBasePrice(100_000);
        dto.setUserId(userId1);

        Event savedEvent = new EventBuilder()
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setEventDate(dto.getEventDate())
                .setLocation(dto.getLocation())
                .setBasePrice(dto.getBasePrice())
                .setUserId(dto.getUserId())
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);


        Event result = eventService.createEvent(dto);


        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }
    @Test
    void createEvent_fail_emptyTitle() {
        // Arrange
        CreateEventDTO dto = new CreateEventDTO();
        dto.setTitle("  ");  // Blank title

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(dto);
        });

        assertEquals("Title cannot be empty", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }
    @Test
    void createEvent_fail_nullTitle() {
        // Arrange
        CreateEventDTO dto = new CreateEventDTO();
        dto.setTitle(null);  // Null title

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(dto);
        });

        assertEquals("Title cannot be empty", exception.getMessage());
        verify(eventRepository, never()).save(any());
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

        ResponseDTO<EventStatus> result = eventService.publishEvent(eventId);

        assertNotNull(result);
        assertEquals(EventStatus.PUBLISHED, result.getData());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCancelEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        ResponseDTO<EventStatus> result = eventService.cancelEvent(eventId);

        assertNotNull(result);
        assertEquals(EventStatus.CANCELLED, result.getData());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCompleteEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);


        ResponseDTO<EventStatus> result = eventService.completeEvent(eventId);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(EventStatus.COMPLETED, result.getData());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testGetEvent_Found() {
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));


        Event result = eventService.getEvent(eventId);

        Assertions.assertEquals(testEvent, result);
        Mockito.verify(eventRepository, Mockito.times(1)).findById(eventId);
    }

    @Test
    void testGetEvent_NotFound() {
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () -> {
            eventService.getEvent(eventId);
        });


        Mockito.verify(eventRepository, Mockito.times(1)).findById(eventId);
    }

    @Test
    void testChangeStatus_EventNotFound() {
        UUID eventId = UUID.randomUUID();
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResponseDTO<EventStatus> result = eventService.publishEvent(eventId);

        assertFalse(result.isSuccess());
        assertEquals("Event not found", result.getMessage());
        assertNull(result.getData());
    }





}