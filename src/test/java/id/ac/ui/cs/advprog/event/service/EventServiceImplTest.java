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
    private UUID eventId2;
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
        eventId2 = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusMonths(4);
        LocalDateTime eventDates = LocalDateTime.now().minusMonths(4);
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
        savedEvent.setId(UUID.randomUUID());
        savedEvent.setTitle(validEventDTO.getTitle());
        savedEvent.setDescription(validEventDTO.getDescription());
        savedEvent.setEventDate(validEventDTO.getEventDate());
        savedEvent.setLocation(validEventDTO.getLocation());
        savedEvent.setBasePrice(validEventDTO.getBasePrice());
        savedEvent.setStatus(EventStatus.DRAFT);
        savedEvent.setUserId(validEventDTO.getUserId());

        userId = UUID.randomUUID();
        publishedEvents = new ArrayList<>();
        publicEvent1 = new Event();
        publicEvent1.setId(UUID.randomUUID());
        publicEvent1.setTitle("Public Event 1");
        publicEvent1.setDescription("Public Description 1");
        publicEvent1.setEventDate(eventDates);
        publicEvent1.setLocation("Public Location 1");
        publicEvent1.setBasePrice(100.0);
        publicEvent1.setStatus(EventStatus.PUBLISHED);
        publicEvent1.setUserId(eventId2);

        publicEvent2 = new Event();
        publicEvent2.setId(UUID.randomUUID());
        publicEvent2.setTitle("Public Event 1");
        publicEvent2.setDescription("Public Description 1");
        publicEvent2.setEventDate(eventDates);
        publicEvent2.setLocation("Public Location 1");
        publicEvent2.setBasePrice(100.0);
        publicEvent2.setStatus(EventStatus.PUBLISHED);
        publicEvent2.setUserId(UUID.randomUUID());

        publishedEvents.add(publicEvent1);
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
        SecurityContextHolder.setContext(context);

    }

    @Test
    void testCreateEvent() {

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

        Event result = eventService.createEvent(dto, userId1);

        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_fail_emptyTitle() {

        CreateEventDTO dto = new CreateEventDTO();
        dto.setTitle("  ");  // Blank title

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(dto, userId1);
        });

        assertEquals("Title cannot be empty", exception.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_fail_nullTitle() {

        CreateEventDTO dto = new CreateEventDTO();
        dto.setTitle(null);  // Null title

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(dto, userId);
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
    void testListEvents_shouldReturnPublishedEvents_whenUserIdIsNull() {

        Event publishedEvent1 = new Event();
        publishedEvent1.setId(UUID.randomUUID());
        publishedEvent1.setTitle("Published Event 1");
        publishedEvent1.setStatus(EventStatus.PUBLISHED);
        publishedEvent1.setUserId(UUID.randomUUID());

        Event publishedEvent2 = new Event();
        publishedEvent2.setId(UUID.randomUUID());
        publishedEvent2.setTitle("Published Event 2");
        publishedEvent2.setStatus(EventStatus.PUBLISHED);
        publishedEvent2.setUserId(UUID.randomUUID());

        List<Event> publishedEvents = Arrays.asList(publishedEvent1, publishedEvent2);

        when(eventRepository.findByStatus(EventStatus.PUBLISHED)).thenReturn(publishedEvents);

        List<Event> result = eventService.listEvents(null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Published Event 1", result.get(0).getTitle());
        assertEquals("Published Event 2", result.get(1).getTitle());
        assertEquals(EventStatus.PUBLISHED, result.get(0).getStatus());
        assertEquals(EventStatus.PUBLISHED, result.get(1).getStatus());

        verify(eventRepository, times(1)).findByStatus(EventStatus.PUBLISHED);
        verify(eventRepository, never()).findOwnOrPublishedEvents(any(UUID.class), any(EventStatus.class));
    }

    @Test
    void testListEvents_shouldReturnOwnAndPublishedEvents_whenUserIdIsProvided() {

        UUID userId = UUID.randomUUID();

        Event ownDraftEvent = new Event();
        ownDraftEvent.setId(UUID.randomUUID());
        ownDraftEvent.setTitle("Own Draft Event");
        ownDraftEvent.setStatus(EventStatus.DRAFT);
        ownDraftEvent.setUserId(userId);

        List<Event> ownOrPublishedEvents = Arrays.asList(ownDraftEvent, publicEvent1);

        when(eventRepository.findOwnOrPublishedEvents(userId, EventStatus.PUBLISHED)).thenReturn(ownOrPublishedEvents);

        List<Event> result = eventService.listEvents(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(ownDraftEvent));
        assertTrue(result.contains(publicEvent1));

        verify(eventRepository, times(1)).findOwnOrPublishedEvents(userId, EventStatus.PUBLISHED);
        verify(eventRepository, never()).findByStatus(any(EventStatus.class));
    }

    @Test
    void testDeleteEvent_shouldDeleteSuccessfully_whenEventIsDraft() {

        Event event = new Event();
        event.setId(eventId);
        event.setStatus(EventStatus.DRAFT); 

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).delete(event); 

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).delete(event); 
    }

    @Test
    void testDeleteEvent_shouldThrowException_whenEventNotFound() {

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Event not found", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).delete(any(Event.class));
    }

    @Test
    void testDeleteEvent_shouldThrowException_whenEventIsPublished() {

        Event publishedEvent = new Event();
        publishedEvent.setId(eventId);
        publishedEvent.setStatus(EventStatus.PUBLISHED); 

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(publishedEvent));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Event refuse to be deleted", exception.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, never()).delete(any(Event.class));
    }

    @Test
    void listEvents_asUser_shouldReturnPublishedEventsOnly() {
        UUID userId = UUID.randomUUID();
        List<Event> mockEvents = List.of(publicEvent1, publicEvent2, testEvent);

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
    void testPublishEvent_pastEvent() {

        publicEvent1.setEventDate(LocalDateTime.now().minusDays(1));
        when(eventRepository.findById(eventId2)).thenReturn(Optional.of(publicEvent1));

        ResponseDTO<EventStatus> result = eventService.publishEvent(eventId2);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
        assertEquals("Cannot publish event with a past date", result.getMessage());
        verify(eventRepository, times(1)).findById(eventId2);
        verify(eventRepository, never()).save(any(Event.class)); 
    }

    @Test
    void testPublishEvent_minimum() {

        publicEvent1.setEventDate(LocalDateTime.now().plusMonths(1));
        when(eventRepository.findById(eventId2)).thenReturn(Optional.of(publicEvent1));

        ResponseDTO<EventStatus> result = eventService.publishEvent(eventId2);

        assertFalse(result.isSuccess());
        assertNull(result.getData());
        assertEquals("Event must be scheduled at least 3 months from now to be published", result.getMessage());
        verify(eventRepository, times(1)).findById(eventId2);
        verify(eventRepository, never()).save(any(Event.class));
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
    void testCompleteEvent_notfound() {

        when(eventRepository.findById(eventId2)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            eventService.completeEvent(eventId2);
        });

        verify(eventRepository, times(1)).findById(eventId2);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testChangeStatus_successfulComplete() {

        Event event = new Event();
        event.setId(eventId);
        event.setStatus(EventStatus.DRAFT); // status awal

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseDTO<EventStatus> result = eventService.completeEvent(eventId);

        assertTrue(result.isSuccess());
        assertEquals(EventStatus.COMPLETED, result.getData());
        assertEquals("Event status changed to COMPLETED", result.getMessage());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(event);
        assertEquals(EventStatus.COMPLETED, event.getStatus()); 
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

        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEvent(eventId);
        });

        Mockito.verify(eventRepository, Mockito.times(1)).findById(eventId);
    }

    @Test
    void testChangeStatus_EventNotFound() {
        UUID eventId = UUID.randomUUID();
        Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EventNotFoundException thrown = assertThrows(EventNotFoundException.class, () -> {
            eventService.publishEvent(eventId);
        });

        assertEquals("Event not found", thrown.getMessage());
    }

}
