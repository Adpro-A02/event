package id.ac.ui.cs.advprog.event.controller;

import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.model.EventBuilder;
import id.ac.ui.cs.advprog.event.service.EventService;
import id.ac.ui.cs.advprog.event.enums.EventStatus;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import id.ac.ui.cs.advprog.event.repository.EventRepository;


class EventControllerTest {

    
    @Mock
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;
    
    @InjectMocks
    private EventController eventController;
    
    
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Event dummyEvent1;
    private Event dummyEvent2;

    private UUID nonExistentId;
    private UUID eventId1;
    private UUID eventId2;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        objectMapper = new ObjectMapper();
        LocalDateTime dates = LocalDateTime.of(2025, 5, 10, 10, 0);
        eventId1 = UUID.randomUUID();
        eventId2 = UUID.randomUUID();
        dummyEvent1 = new EventBuilder()
           .setTitle("Event 1")
           .setId(eventId1)
           .setDescription("Description")
           .setEventDate(dates)
           .setLocation("Jakarta")
           .setBasePrice(0.0)
           .build();

        

        dummyEvent2 = new EventBuilder()
        .setTitle("Event 2")
        .setId(eventId2)
        .setDescription("Description")
        .setEventDate(dates)
        .setLocation("Depok")
        .setBasePrice(0.0)
        .build();

        eventRepository.save(dummyEvent1);
        eventRepository.save(dummyEvent2);

        nonExistentId = UUID.randomUUID();
       
    }

   
     @Test
    public void testCreateEvent_Success() throws Exception {
        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();
        Event eventToCreate = new Event();
        eventToCreate.setTitle("Tech Conference 2025");
        eventToCreate.setId(eventId2);
        eventToCreate.setDescription("Annual technology conference");
        eventToCreate.setEventDate(LocalDateTime.of(2025, 6, 15, 9, 0));
        eventToCreate.setLocation("Convention Center");
        
        Event createdEvent = new Event();
        createdEvent.setId(eventId1);
        createdEvent.setTitle("Tech Conference 2025");
        createdEvent.setDescription("Annual technology conference");
        createdEvent.setEventDate(LocalDateTime.of(2025, 6, 15, 9, 0));
        createdEvent.setLocation("Convention Center");
        
        when(eventService.createEvent(any(Event.class))).thenReturn(createdEvent);
        
      
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tech Conference 2025"))
                .andExpect(jsonPath("$.location").value("Convention Center"));
    }
    @Test
    void testGetAllEvents() throws Exception {
        
      
        when(eventService.listEvents()).thenReturn(Arrays.asList(dummyEvent1, dummyEvent2));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Event 1"))
                .andExpect(jsonPath("$[1].title").value("Event 2"));
    }

    @Test
    void testGetEventById() throws Exception {
        
        
        Mockito.when(eventService.getEvent(eventId1)).thenReturn(dummyEvent1);

        mockMvc.perform(get("/api/events/{id}", eventId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Event 1"))
                .andExpect(jsonPath("$.location").value("Jakarta"));
    }

    @Test
    void testUpdateEvent() throws Exception {
        LocalDateTime dates = LocalDateTime.of(2025, 5, 10, 10, 0);
        UpdateEventDTO updateDTO = new UpdateEventDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setEventDate(dates);
        updateDTO.setLocation("Updated Jakarta");
        updateDTO.setBasePrice(200.0);
        updateDTO.setStatus(EventStatus.PUBLISHED);
       
        
        when(eventService.updateEvent(eq(eventId1), any(UpdateEventDTO.class))).thenReturn(updateDTO);

        mockMvc.perform(put("/api/events/{id}", eventId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Title\", \"location\": \"Updated Jakarta\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.location").value("Updated Jakarta"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);
    
       Event event = new EventBuilder()
           .setTitle("Event 1")
           .setId(eventId)
           .setDescription("Description")
           .setEventDate(date)
           .setLocation("Jakarta")
           .setBasePrice(0.0)
           .build();
           
        doNothing().when(eventService).deleteEvent(eventId);

        mockMvc.perform(delete("/api/events/{id}", eventId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetEventsByDate() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 10, 10, 0);
        LocalDate date = dateTime.toLocalDate();
       
        when(eventService.getEventByDate(date)).thenReturn(Arrays.asList(dummyEvent1,dummyEvent2));

        mockMvc.perform(get("/api/events/date/{date}", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Event 1"))
                .andExpect(jsonPath("$[0].location").value("Jakarta"));
    }

    @Test
    void testPublishEvent() throws Exception {
        LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);
        UUID eventId = UUID.randomUUID();
        
        
        Event draftEvent = new Event();
        draftEvent.setId(eventId);
        draftEvent.setTitle("Indonesia");
        draftEvent.setDescription("Description");
        draftEvent.setEventDate(date);
        draftEvent.setLocation("Jakarta");
        draftEvent.setBasePrice(0.0);
        draftEvent.setStatus(EventStatus.DRAFT);

        Event completeEvent = new Event();
        completeEvent.setId(eventId);
        completeEvent.setTitle("Indonesia");
        completeEvent.setDescription("Description");
        completeEvent.setEventDate(date);
        completeEvent.setLocation("Jakarta");
        completeEvent.setBasePrice(0.0);
        completeEvent.setStatus(EventStatus.PUBLISHED);
        eventRepository.save(draftEvent);

      

        doReturn(completeEvent).when(eventService).publishEvent(eventId);

        mockMvc.perform(patch("/api/events/{id}/publish", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void testCancelEvent() throws Exception {
        LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);
        UUID eventId = UUID.randomUUID();
        
        
        Event draftEvent = new Event();
        draftEvent.setId(eventId);
        draftEvent.setTitle("Indonesia");
        draftEvent.setDescription("Description");
        draftEvent.setEventDate(date);
        draftEvent.setLocation("Jakarta");
        draftEvent.setBasePrice(0.0);
        draftEvent.setStatus(EventStatus.DRAFT);

        
        Event cancelledEvent = new Event();
        cancelledEvent.setId(eventId);
        cancelledEvent.setTitle("Indonesia");
        cancelledEvent.setDescription("Description");
        cancelledEvent.setEventDate(date);
        cancelledEvent.setLocation("Jakarta");
        cancelledEvent.setBasePrice(0.0);
        cancelledEvent.setStatus(EventStatus.CANCELLED);
        
      
        eventRepository.save(draftEvent);
        
        
        doReturn(cancelledEvent).when(eventService).cancelEvent(eventId);

       
        mockMvc.perform(patch("/api/events/{id}/cancel", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        
    }

    @Test
    void testCompleteEvent() throws Exception {
        LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);
        UUID eventId = UUID.randomUUID();
        
        
        Event draftEvent = new Event();
        draftEvent.setId(eventId);
        draftEvent.setTitle("Indonesia");
        draftEvent.setDescription("Description");
        draftEvent.setEventDate(date);
        draftEvent.setLocation("Jakarta");
        draftEvent.setBasePrice(0.0);
        draftEvent.setStatus(EventStatus.DRAFT);


        Event completeEvent = new Event();
        completeEvent.setId(eventId);
        completeEvent.setTitle("Indonesia");
        completeEvent.setDescription("Description");
        completeEvent.setEventDate(date);
        completeEvent.setLocation("Jakarta");
        completeEvent.setBasePrice(0.0);
        completeEvent.setStatus(EventStatus.COMPLETED);
           
        eventRepository.save(draftEvent);

        doReturn(completeEvent).when(eventService).completeEvent(eventId);

        mockMvc.perform(patch("/api/events/{id}/complete", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testGetUpcomingEvents() throws Exception {
        
        when(eventService.getUpcomingEvents()).thenReturn(Arrays.asList(dummyEvent1, dummyEvent2));

        mockMvc.perform(get("/api/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Event 1"))
                .andExpect(jsonPath("$[1].title").value("Event 2"));
    }

    @Test
    void testGetEventById_NotFound() throws Exception {
        UUID eventId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Mockito.when(eventService.getEvent(eventId)).thenReturn(null);


        mockMvc.perform(get("/api/events/{id}", eventId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Event not found"));
}
    @Test
    void testUpdateEvent_NotFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setTitle("Updated Title");

        Mockito.when(eventService.updateEvent(eq(eventId), any(UpdateEventDTO.class)))
            .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(put("/api/events/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Event not found"));
    }
    @Test
    void testPublishEvent_NotFound() throws Exception {
        UUID eventIds = UUID.randomUUID();
        Mockito.when(eventService.publishEvent(eventIds))
            .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(patch("/api/events/{id}/publish", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Event not found"));
    }
    @Test
    void testCancelEvent_NotFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        Mockito.when(eventService.cancelEvent(eventId))
            .thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(patch("/api/events/{id}/cancel", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Event not found"));
    }
     

}
