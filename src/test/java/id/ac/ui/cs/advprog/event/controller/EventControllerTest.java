package id.ac.ui.cs.advprog.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.service.EventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // This helps with serializing Java 8 date/time classes
    }

    @Test
    void createEvent_success() throws Exception {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setLocation("UI");
        event.setEventDate(LocalDateTime.of(2025, 5, 13, 10, 30));
        event.setBasePrice(100.0);
        event.setUserId(UUID.randomUUID());

        Mockito.when(eventService.createEvent(Mockito.any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Event"))
                .andExpect(jsonPath("$.location").value("UI"))
                .andExpect(jsonPath("$.basePrice").value(100.0));
    }

    @Test
    void getAllEvents_success() throws Exception {
        List<Event> events = List.of(new Event(), new Event());
        Mockito.when(eventService.listEvents()).thenReturn(events);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getEventById_success() throws Exception {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        event.setTitle("Found Event");

        Mockito.when(eventService.getEvent(id)).thenReturn(event);

        mockMvc.perform(get("/api/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Found Event"));
    }

    @Test
    void updateEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setTitle("Updated Event");

        Mockito.when(eventService.updateEvent(Mockito.eq(id), Mockito.any(UpdateEventDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/events/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Event"));
    }

    @Test
    void deleteEvent_success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/events/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void getEventsByDate_success() throws Exception {
        LocalDate date = LocalDate.now();
        List<Event> events = List.of(new Event(), new Event());

        Mockito.when(eventService.getEventByDate(date)).thenReturn(events);

        mockMvc.perform(get("/api/events/date/{date}", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUpcomingEvents_success() throws Exception {
        List<Event> events = List.of(new Event(), new Event(), new Event());
        Mockito.when(eventService.getUpcomingEvents()).thenReturn(events);

        mockMvc.perform(get("/api/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void publishEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        ResponseDTO<EventStatus> response = ResponseDTO.<EventStatus>builder()
                .success(true)
                .data(EventStatus.PUBLISHED)
                .build();

        Mockito.when(eventService.publishEvent(id)).thenReturn(response);

        mockMvc.perform(patch("/api/events/{id}/publish", id))
                .andExpect(status().isOk())
                .andExpect(content().string("\"PUBLISHED\""));
    }

    @Test
    void cancelEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        ResponseDTO<EventStatus> response = ResponseDTO.<EventStatus>builder()
                .success(true)
                .data(EventStatus.CANCELLED)
                .build();

        Mockito.when(eventService.cancelEvent(id)).thenReturn(response);

        mockMvc.perform(patch("/api/events/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(content().string("\"CANCELLED\""));
    }

    @Test
    void completeEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        ResponseDTO<EventStatus> response = ResponseDTO.<EventStatus>builder()
                .success(true)
                .data(EventStatus.COMPLETED)
                .build();

        Mockito.when(eventService.completeEvent(id)).thenReturn(response);

        mockMvc.perform(patch("/api/events/{id}/complete", id))
                .andExpect(status().isOk())
                .andExpect(content().string("\"COMPLETED\""));
    }
}