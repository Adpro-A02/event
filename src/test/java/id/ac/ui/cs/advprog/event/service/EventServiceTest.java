package id.ac.ui.cs.advprog.event.service;

import id.ac.ui.cs.advprog.event.model.EventBuilder;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.repository.EventRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;


    private UUID eventId;
    private Event testEvent;
    private UpdateEventDTO updateEventDto;
    private LocalDateTime eventDate;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusDays(10);
        
        testEvent = new EventBuilder();
        testEvent.setId(eventId);
        testEvent.setTitle("Pesta Pora");
        testEvent.setDescription("Test Description");
        testEvent.setEventDate(eventDate);
        testEvent.setLocation("Fasilkom UI");
        testEvent.setBasePrice(100000.0);
        testEvent.setStatus(EventStatus.DRAFT);

        updateEventDto = new UpdateEventDTO();
        updateEventDto.setTitle("Pesta Masyarakat");
        updateEventDto.setDescription("Test Description");
        updateEventDto.setEventDate(eventDate);
        updateEventDto.setLocation("Fasilkom UI");
        updateEventDto.setBasePrice(100000.0);
        updateEventDto.setStatus(EventStatus.DRAFT);

    }

    @Test
    void createEvent_ShouldSetStatusToDraftAndSave() {
        
        Event inputEvent = new EventBuilder();
        inputEvent.setTitle("Pesta Rakyat");
        inputEvent.setEventDate(eventDate);
        inputEvent.setLocation("New Location");
        inputEvent.setBasePrice(200000.0);
        
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event savedEvent = invocation.getArgument(0);
            savedEvent.setId(UUID.randomUUID());
            return savedEvent;
        });

        Event result = eventService.createEvent(inputEvent);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Pesta Rakyat", result.getTitle());
        assertEquals(EventStatus.DRAFT, result.getStatus());
        verify(eventRepository).save(inputEvent);
    }

    @Test
    void listEvents_ShouldReturnAllEvents() {

        Event event1 = new EventBuilder();
        event1.setId(UUID.randomUUID());
        Event event2 = new EventBuilder();
        event2.setId(UUID.randomUUID());
        
        List<Event> eventList = Arrays.asList(event1, event2);
        when(eventRepository.findAll()).thenReturn(eventList);


        List<Event> result = eventService.listEvents();

        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void getEvent_WithExistingId_ShouldReturnEvent() {
 
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

      
        Optional<Event> result = eventService.getEvent(eventId);

        assertTrue(result.isPresent());
        assertEquals(eventId, result.get().getId());
        verify(eventRepository).findById(eventId);
    }

    @Test
    void getEvent_WithNonExistingId_ShouldReturnEmpty() {
      
        UUID nonExistingId = UUID.randomUUID();
        when(eventRepository.findById(nonExistingId)).thenReturn(Optional.empty());


        Optional<Event> result = eventService.getEvent(nonExistingId);

   
        assertFalse(result.isPresent());
        verify(eventRepository).findById(nonExistingId);
    }

    @Test
    void updateEvent_WithExistingId_ShouldUpdateAndReturnEvent() {
    
       
        
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

     
        Event result = eventService.updateEvent(eventId, updateEventDto);

 
        assertNotNull(result);
        assertEquals("Pesta Masyarakat", result.getTitle());
        assertEquals("Test Description", result.getDescription());
        assertEquals(eventDate, result.getEventDate());
        assertEquals("Fasilkom UI", result.getLocation());
        assertEquals(100000.0, result.getBasePrice());
        assertEquals(EventStatus.DRAFT, result.getStatus());
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(testEvent);
    }
    @Test
    void updateEvent_WhenStatusIsNotDraft_ShouldNotAllowDateChange() {
        testEvent.setStatus(EventStatus.PUBLISHED); // Status selain DRAFT
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        updateEventDto.setEventDate(LocalDate.of(2030, 1, 1)); 

        assertThrows(IllegalStateException.class, () -> {
            eventService.updateEvent(eventId, updateEventDto);
        });

        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
    }
    @Test
    void updateEvent_WithNonExistingId_ShouldThrowException() {
        Long nonExistingEventId = 999L;
        when(eventRepository.findById(nonExistingEventId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eventService.updateEvent(nonExistingEventId, updateEventDto);
        });

        verify(eventRepository).findById(nonExistingEventId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void updateEvent_WithNonExistingId_ShouldReturnNull() {
      
        UUID nonExistingId = UUID.randomUUID();
        when(eventRepository.findById(nonExistingId)).thenReturn(Optional.empty());

    
        Event result = eventService.updateEvent(nonExistingId, new Event());

      
        assertNull(result);
        verify(eventRepository).findById(nonExistingId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void deleteEvent_WithExistingId_ShouldDeleteAndReturnTrue() {
      
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        doNothing().when(eventRepository).deleteById(eventId);

        boolean result = eventService.deleteEvent(eventId);

     
        assertTrue(result);
        verify(eventRepository).findById(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void deleteEvent_WithNonExistingId_ShouldReturnFalse() {
       
        UUID nonExistingId = UUID.randomUUID();
        when(eventRepository.findById(nonExistingId)).thenReturn(Optional.empty());

      
        boolean result = eventService.deleteEvent(nonExistingId);

       
        assertFalse(result);
        verify(eventRepository).findById(nonExistingId);
        verify(eventRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void publishEvent_WithDraftEvent_ShouldUpdateStatusAndReturnEvent() {
       
        testEvent.setStatus(EventStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        
        Event result = eventService.publishEvent(eventId);

      
        assertNotNull(result);
        assertEquals(EventStatus.PUBLISHED, result.getStatus());
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(testEvent);
    }

    void publishEvent_WithDraftEvent_ShouldUpdateStatusAndReturnEvent() {
    
    LocalDateTime publishDate = LocalDateTime.now();
    LocalDateTime validEventDate = publishDate.plusMonths(3).plusDays(1); 
    
    testEvent.setStatus(EventStatus.DRAFT);
    testEvent.setEventDate(validEventDate);
    testEvent.setBasePrice(100000.0); 
    
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
    when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
    Event result = eventService.publishEvent(eventId);
    
    assertNotNull(result);
    assertEquals(EventStatus.PUBLISHED, result.getStatus());
    verify(eventRepository).findById(eventId);
    verify(eventRepository).save(testEvent);
}

@Test
void publishEvent_WithInvalidEventDate_ShouldThrowException() {
    
    LocalDateTime publishDate = LocalDateTime.now();
    LocalDateTime invalidEventDate = publishDate.plusMonths(2); // Only 2 months in future
    
    testEvent.setStatus(EventStatus.DRAFT);
    testEvent.setEventDate(invalidEventDate);
    testEvent.setBasePrice(100000.0);
    
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
    
  
    assertThrows(IllegalStateException.class, () -> {
        eventService.publishEvent(eventId);
    });
    
    verify(eventRepository).findById(eventId);
    verify(eventRepository, never()).save(any(Event.class));
}

@Test
void publishEvent_WithInvalidPrice_ShouldThrowException() {
    
    LocalDateTime publishDate = LocalDateTime.now();
    LocalDateTime validEventDate = publishDate.plusMonths(3).plusDays(1);
    
    testEvent.setStatus(EventStatus.DRAFT);
    testEvent.setEventDate(validEventDate);
    testEvent.setBasePrice(-1); 
    
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
    
   
    assertThrows(IllegalStateException.class, () -> {
        eventService.publishEvent(eventId);
    });
    
    verify(eventRepository).findById(eventId);
    verify(eventRepository, never()).save(any(Event.class));
}

    @Test
    void cancelEvent_WithPublishedEvent_ShouldUpdateStatusAndReturnEvent() {
      
        testEvent.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        
        Event result = eventService.cancelEvent(eventId);

      
        assertNotNull(result);
        assertEquals(EventStatus.CANCELLED, result.getStatus());
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(testEvent);
    }

    @Test
    void completeEvent_WithPublishedEvent_ShouldUpdateStatusAndReturnEvent() {
      
        testEvent.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

      
        Event result = eventService.completeEvent(eventId);

      
        assertNotNull(result);
        assertEquals(EventStatus.COMPLETED, result.getStatus());
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(testEvent);
    }

    @Test
    void cancelEvent_WithCancelledEvent_ShouldThrowException() {
      
        testEvent.setStatus(EventStatus.CANCELLED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

    
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            eventService.cancelEvent(eventId);
        });
        
        assertEquals("Cannot change status of a cancelled event", exception.getMessage());
        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void completeEvent_WithCompletedEvent_ShouldThrowException() {
        
        testEvent.setStatus(EventStatus.COMPLETED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

       
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            eventService.completeEvent(eventId);
        });
        
        assertEquals("Cannot change status of a completed event", exception.getMessage());
        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
    }
}