package id.ac.ui.cs.advprog.event.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import id.ac.ui.cs.advprog.event.dto.CreateEventDTO;
import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.exception.EventNotFoundException;
import id.ac.ui.cs.advprog.event.model.EventBuilder;
import jakarta.transaction.Transactional;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@Transactional
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;
    @Mock
    private Authentication authentication;

    private ArgumentCaptor<Event> eventCaptor;
    private Event testEvent;
    private CreateEventDTO validEventDTO;
    private Event savedEvent;
    private UUID eventId;
    private LocalDateTime eventDate;
    private UUID userId;
    private Event publicEvent1;
    private Event publicEvent2;
    private SecurityContext securityContext;
    private UUID userId1;
    private List<Event> publishedEvents;
    private List<Event> organizerEvents;

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

        userId = UUID.randomUUID();
        publishedEvents = new ArrayList<>();
        publicEvent1 = new Event();
        publicEvent1.setId(UUID.randomUUID());
        publicEvent1.setTitle("Public Event 1");
        publicEvent1.setDescription("Public Description 1");
        publicEvent1.setEventDate(eventDate);
        publicEvent1.setLocation("Public Location 1");
        publicEvent1.setBasePrice(100.0);
        publicEvent1.setStatus(EventStatus.PUBLISHED);
        publicEvent1.setUserId(UUID.randomUUID());

        publicEvent2 = new Event();
        publicEvent2.setId(UUID.randomUUID());
        publicEvent2.setTitle("Public Event 2");
        publicEvent2.setDescription("Public Description 2");
        publicEvent2.setEventDate(eventDate);
        publicEvent2.setLocation("Public Location 2");
        publicEvent2.setBasePrice(150.0);
        publicEvent2.setStatus(EventStatus.PUBLISHED);
        publicEvent2.setUserId(UUID.randomUUID());

        publishedEvents.add(publicEvent1);
        publishedEvents.add(publicEvent2);

        organizerEvents = new ArrayList<>();

        Event organizerEvent1 = new Event();
        organizerEvent1.setId(UUID.randomUUID());
        organizerEvent1.setTitle("Organizer Event 1");
        organizerEvent1.setDescription("Organizer Description 1");
        organizerEvent1.setEventDate(eventDate);
        organizerEvent1.setLocation("Organizer Location 1");
        organizerEvent1.setBasePrice(200.0);
        organizerEvent1.setStatus(EventStatus.PUBLISHED);
        organizerEvent1.setUserId(userId);

        Event organizerEvent2 = new Event();
        organizerEvent2.setId(UUID.randomUUID());
        organizerEvent2.setTitle("Organizer Event 2");
        organizerEvent2.setDescription("Organizer Description 2");
        organizerEvent2.setEventDate(eventDate);
        organizerEvent2.setLocation("Organizer Location 2");
        organizerEvent2.setBasePrice(250.0);
        organizerEvent2.setStatus(EventStatus.PUBLISHED);
        organizerEvent2.setUserId(userId);

        organizerEvents.add(organizerEvent1);
        organizerEvents.add(organizerEvent2);

        SecurityContext context = mock(SecurityContext.class);
//        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);



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


        Event result = eventService.createEvent(dto,userId1);


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
            eventService.createEvent(dto,userId1);
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
            eventService.createEvent(dto,userId);
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
        // Arrange
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setEventDate(eventDate);
        updateDTO.setLocation("Updated Location");
        updateDTO.setBasePrice(200.0);
        updateDTO.setStatus(EventStatus.PUBLISHED);

        testEvent.setStatus(EventStatus.PUBLISHED);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.updateEvent(eventId, updateDTO);
        });

        assertEquals("Published event restriction cannot be updated", exception.getMessage());
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
    void listEvents_asOrganizer_shouldReturnOwnOrPublishedEvents() {
        UUID userId = UUID.randomUUID();
        String role = "Organizer";
        List<Event> mockEvents = List.of(new Event(), new Event());

        when(eventRepository.findOwnOrPublishedEvents(userId, EventStatus.PUBLISHED))
                .thenReturn(mockEvents);

        List<Event> result = eventService.listEvents(userId);

        assertEquals(2, result.size());
        verify(eventRepository).findOwnOrPublishedEvents(userId, EventStatus.PUBLISHED);
        verify(eventRepository, never()).findByStatus(any());
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
        // Arrange
        Event event = new Event();  // atau mock Event sesuai kebutuhan
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).deleteById(eventId);

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).deleteById(eventId);
    }
    @Test
    void listEvents_asUser_shouldReturnPublishedEventsOnly() {
        UUID userId = UUID.randomUUID();
        List<Event> mockEvents = List.of(publicEvent1, publicEvent2, testEvent);

        // Mock the actual method being called
        when(eventRepository.findOwnOrPublishedEvents(userId, EventStatus.PUBLISHED))
                .thenReturn(mockEvents);


        List<Event> result = eventService.listEvents(userId);


        assertEquals(3, result.size());
        assertTrue(result.contains(publicEvent1));
        assertTrue(result.contains(publicEvent2));
        assertTrue(result.contains(testEvent));

        verify(eventRepository).findOwnOrPublishedEvents(userId, EventStatus.PUBLISHED);
        verify(eventRepository, never()).findByStatus(any());
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
    void testGetEvent_Found() throws Exception {
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

        // Assert bahwa exception dilempar
        EventNotFoundException thrown = assertThrows(EventNotFoundException.class, () -> {
            eventService.publishEvent(eventId);
        });

        assertEquals("Event not found", thrown.getMessage());
    }





}