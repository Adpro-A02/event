package id.ac.ui.cs.advprog.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.event.dto.CreateEventDTO;
import id.ac.ui.cs.advprog.event.dto.UpdateEventDTO;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.exception.EventNotFoundException;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.dto.ResponseDTO;
import id.ac.ui.cs.advprog.event.security.JwtTokenProvider;
import id.ac.ui.cs.advprog.event.service.EventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(EventController.class)
@Import(id.ac.ui.cs.advprog.event.config.SecurityConfig.class)
@TestPropertySource(properties = {
        "CORS_ALLOWED_ORIGIN=http://localhost:3000 "
})
public class EventControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(EventControllerTest.class);

    @MockBean
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;



    @Autowired
    private ObjectMapper objectMapper;

    private UUID userUuid;
    private CreateEventDTO validDto;

    private Event event1;
    private Event event2;
    private UUID organizerId;
    private List<Event> mockEvents;
    @BeforeEach
    void setUp() {
        organizerId = UUID.fromString("c64ee53e-f39b-4ec8-9288-3318b0b8a97e");
        objectMapper.findAndRegisterModules(); // For handling Java 8 date/time types
        userUuid = UUID.fromString("c64ee53e-f39b-4ec8-9288-3318b0b8a97e");
        validDto = new CreateEventDTO();
        validDto.setTitle("Test Event");
        validDto.setDescription("Description");
        validDto.setLocation("Depok");
        validDto.setEventDate(LocalDateTime.now().plusDays(1));
        validDto.setBasePrice(50.0);

        validDto.setUserId(userUuid);

        event1 = new Event();
        event1.setId(UUID.randomUUID());
        event1.setTitle("Test Event 1");
        event1.setEventDate(LocalDateTime.now().plusDays(7));
        event1.setLocation("Depok");
        event1.setDescription("Test Description 1");
        event1.setBasePrice(100.0);
        event1.setStatus(EventStatus.DRAFT);
        event1.setUserId(organizerId);

        event2 = new Event();
        event2.setId(UUID.randomUUID());
        event2.setTitle("Test Event 2");
        event2.setEventDate(LocalDateTime.now().plusDays(14));
        event2.setLocation("Jakarta");
        event2.setDescription("Test Description 2");
        event2.setBasePrice(200.0);
        event2.setStatus(EventStatus.PUBLISHED);
        event2.setUserId(organizerId);

        mockEvents = Arrays.asList(event1, event2);

    }

    @Test
    @WithMockUser(username = "c64ee53e-f39b-4ec8-9288-3318b0b8a97e", authorities = "Organizer")
    public void testCreateEvent_Success() throws Exception {
        UUID userId = UUID.fromString("c64ee53e-f39b-4ec8-9288-3318b0b8a97e");

        CreateEventDTO createEventDTO = new CreateEventDTO();
        createEventDTO.setTitle("Seminar Fasilkom");
        createEventDTO.setDescription("Event pembelajaran untuk mahasiswa.");
        createEventDTO.setEventDate(LocalDate.now().plusDays(3).atStartOfDay());
        createEventDTO.setLocation("Aula Fasilkom");
        createEventDTO.setBasePrice(0.0);

        Event mockEvent = new Event();
        mockEvent.setId(UUID.randomUUID());
        mockEvent.setTitle(createEventDTO.getTitle());
        mockEvent.setDescription(createEventDTO.getDescription());
        mockEvent.setEventDate(createEventDTO.getEventDate());
        mockEvent.setLocation(createEventDTO.getLocation());
        mockEvent.setBasePrice(createEventDTO.getBasePrice());

        when(eventService.createEvent(any(CreateEventDTO.class), eq(userId))).thenReturn(mockEvent);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createEventDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(createEventDTO.getTitle()))
                .andExpect(jsonPath("$.location").value(createEventDTO.getLocation()));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void getEventById_success() throws Exception {
        UUID id = UUID.randomUUID();
        Event event = new Event();
        event.setId(id);
        event.setTitle("Found Event");

        when(eventService.getEvent(id)).thenReturn(event);

        mockMvc.perform(get("/api/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Found Event"));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void updateEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setTitle("Updated Event");

        when(eventService.updateEvent(eq(id), any(UpdateEventDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/events/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Event"));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void deleteEvent_success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/events/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void getEventsByDate_success() throws Exception {
        LocalDate date = LocalDate.from(LocalDateTime.now().plusDays(1));
        List<Event> events = List.of(new Event(), new Event());

        when(eventService.getEventByDate(LocalDate.from(date))).thenReturn(events);

        mockMvc.perform(get("/api/events/date/{date}", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    @WithMockUser(authorities = "Organizer")
    void publishEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        ResponseDTO<EventStatus> response = ResponseDTO.<EventStatus>builder()
                .success(true)
                .data(EventStatus.PUBLISHED)
                .build();

        when(eventService.publishEvent(id)).thenReturn(CompletableFuture.completedFuture(response));


        MvcResult mvcResult = mockMvc.perform(patch("/api/events/{id}/publish", id))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("\"PUBLISHED\""));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void publishEvent_runtimeException() throws Exception {
        UUID id = UUID.randomUUID();


        when(eventService.publishEvent(id))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("Unexpected error")));

        MvcResult mvcResult = mockMvc.perform(patch("/api/events/{id}/publish", id))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An unexpected error occurred")));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void publishEvent_unsuccessful_shouldReturn400BadRequest() throws Exception {
        UUID id = UUID.randomUUID();


        ResponseDTO<EventStatus> failedResponse = ResponseDTO.<EventStatus>builder()
                .success(false)
                .message("Event not found")
                .build();

        when(eventService.publishEvent(id))
                .thenReturn(CompletableFuture.completedFuture(failedResponse));

        MvcResult mvcResult = mockMvc.perform(patch("/api/events/{id}/publish", id))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void publishEvent_eventNotFound() throws Exception {
        UUID id = UUID.randomUUID();


        when(eventService.publishEvent(id))
                .thenReturn(CompletableFuture.failedFuture(new EventNotFoundException("Event not found")));

        MvcResult mvcResult = mockMvc.perform(patch("/api/events/{id}/publish", id))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Event not found")));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void cancelEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        ResponseDTO<EventStatus> response = ResponseDTO.<EventStatus>builder()
                .success(true)
                .data(EventStatus.CANCELLED)
                .build();

        when(eventService.cancelEvent(id)).thenReturn(response);

        mockMvc.perform(patch("/api/events/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(content().string("\"CANCELLED\""));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void completeEvent_success() throws Exception {
        UUID id = UUID.randomUUID();
        ResponseDTO<EventStatus> response = ResponseDTO.<EventStatus>builder()
                .success(true)
                .data(EventStatus.COMPLETED)
                .build();

        when(eventService.completeEvent(id)).thenReturn(response);

        mockMvc.perform(patch("/api/events/{id}/complete", id))
                .andExpect(status().isOk())
                .andExpect(content().string("\"COMPLETED\""));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void createEvent_shouldReturnBadRequest_whenMissingFields() throws Exception {
        CreateEventDTO invalidDto = new CreateEventDTO();
        invalidDto.setTitle("");
        invalidDto.setLocation("Location");
        invalidDto.setEventDate(null);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void getEventById_shouldReturnNotFound_whenEventDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(eventService.getEvent(id)).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(get("/api/events/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Event not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void updateEvent_shouldReturnBadRequest_whenInvalidInput() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setTitle("");

        when(eventService.updateEvent(eq(id), any(UpdateEventDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid title"));

        mockMvc.perform(put("/api/events/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void updateEvent_shouldReturnNotFound_whenEventDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateEventDTO dto = new UpdateEventDTO();
        dto.setTitle("Any Title");

        when(eventService.updateEvent(eq(id), any(UpdateEventDTO.class)))
                .thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(put("/api/events/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void publishEvent_shouldReturnNotFound_whenEventDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(eventService.publishEvent(id)).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(patch("/api/events/{id}/publish", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void cancelEvent_shouldReturnNotFound_whenEventDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(eventService.cancelEvent(id)).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(patch("/api/events/{id}/cancel", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void completeEvent_shouldReturnNotFound_whenEventDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(eventService.completeEvent(id)).thenThrow(new EventNotFoundException("Event not found"));

        mockMvc.perform(patch("/api/events/{id}/complete", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void createEvent_shouldReturnBadRequest_whenTitleMissing() throws Exception {
        CreateEventDTO invalidDto = new CreateEventDTO();
        invalidDto.setEventDate(LocalDateTime.of(2025, 5, 13, 10, 30));
        invalidDto.setTitle("   ");  // blank after trim
        invalidDto.setLocation("Some Location");
        invalidDto.setBasePrice(50.0);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void createEvent_shouldReturnBadRequest_whenLocationMissing() throws Exception {
        CreateEventDTO invalidDto = new CreateEventDTO();
        invalidDto.setEventDate(LocalDateTime.of(2025, 5, 13, 10, 30));
        invalidDto.setTitle("Valid Title");
        invalidDto.setLocation(null);
        invalidDto.setBasePrice(75.0);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "c64ee53e-f39b-4ec8-9288-3318b0b8a97e", authorities = "Organizer")
    void whenCreateEventValid_thenReturns201() throws Exception {
        Event mockEvent = new Event();
        mockEvent.setTitle(validDto.getTitle());
        mockEvent.setDescription(validDto.getDescription());
        mockEvent.setLocation(validDto.getLocation());
        mockEvent.setEventDate(validDto.getEventDate());
        mockEvent.setBasePrice(validDto.getBasePrice());

        when(eventService.createEvent(any(CreateEventDTO.class), eq(userUuid))).thenReturn(mockEvent);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(validDto.getTitle()))
                .andExpect(jsonPath("$.description").value(validDto.getDescription()))
                .andExpect(jsonPath("$.location").value(validDto.getLocation()))
                //                .andExpect(jsonPath("$.eventDate").exists())
                .andExpect(jsonPath("$.basePrice").value(validDto.getBasePrice()));
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void whenTitleIsEmpty_thenReturns400() throws Exception {
        validDto.setTitle("   ");

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void whenEventDateIsNull_thenReturns400() throws Exception {
        validDto.setEventDate(null);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "Organizer")
    void deleteEvent_InvalidUUID() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new EventNotFoundException("Event not found"))
                .when(eventService).deleteEvent(id);

        mockMvc.perform(delete("/api/events/{id}", id))
                .andExpect(status().isNotFound());

        verify(eventService).deleteEvent(id);
    }

    @Test
    void getAllEvents_shouldReturnOk_whenNoAuthentication() throws Exception {
       
        List<Event> mockEvents = Arrays.asList(
                new Event(),
                new Event()
        );

        when(eventService.listEvents(null)).thenReturn(mockEvents);

        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(eventService).listEvents(null);
    }

    @Test
    void getAllEvents_shouldReturnOk_whenValidAuthentication() throws Exception {
        
        UUID userId = UUID.randomUUID();
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(mockAuth.getName()).thenReturn(userId.toString());
        when(mockAuth.getPrincipal()).thenReturn("user");

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
        SecurityContextHolder.setContext(mockSecurityContext);

        // Mock data untuk response
        List<Event> mockEvents = Arrays.asList(
                new Event(),
                new Event()
        );
                
        when(eventService.listEvents(userId)).thenReturn(mockEvents);

        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        verify(eventService).listEvents(userId);
    }


    @Test
    @WithMockUser(authorities = "Organizer")
    void deleteEvent_IllegalArgumentException() throws Exception {
        Event publishedEvent = new Event();
        UUID id = UUID.randomUUID();

        publishedEvent.setId(id);
        publishedEvent.setTitle("Published Event");
        publishedEvent.setDescription("This event has already been published");
        publishedEvent.setLocation("Online");
        publishedEvent.setStatus(EventStatus.PUBLISHED);



        doThrow(new IllegalArgumentException("Event resuse to delete"))
                .when(eventService).deleteEvent(id);

        mockMvc.perform(delete("/api/events/{id}", id))

                .andExpect(status().isBadRequest());

        verify(eventService).deleteEvent(id);
    }
    @Test
    @WithMockUser(authorities = "Organizer")
    void deleteEvent_not_found() throws Exception {
        Event publishedEvent = new Event();
        UUID id = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        publishedEvent.setId(id);
        publishedEvent.setTitle("Published Event");
        publishedEvent.setDescription("This event has already been published");
        publishedEvent.setLocation("Online");
        publishedEvent.setStatus(EventStatus.PUBLISHED);



        doThrow(new EventNotFoundException("Event not found"))
                .when(eventService).deleteEvent(id2);

        mockMvc.perform(delete("/api/events/{id}", id2))
 
               .andExpect(status().isNotFound());

        verify(eventService).deleteEvent(id2);
    }
    @Test
    void getAllEvents_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {

        SecurityContextHolder.clearContext();


        when(eventService.listEvents(null)).thenThrow(new RuntimeException("Simulated failure"));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed get event"));

        verify(eventService).listEvents(null);
    }

    @Test
    @WithMockUser(username = "c64ee53e-f39b-4ec8-9288-3318b0b8a97e", authorities = "Organizer")
    void getMyEvents_Success_ReturnsEventsList() throws Exception {
        // Arrange
        when(eventService.listEventsByOrganizer(any(UUID.class))).thenReturn(mockEvents);

        // Act & Assert
        mockMvc.perform(get("/api/events/organizer/my-events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.events").isArray())
                .andExpect(jsonPath("$.data.events.length()").value(2))
                .andExpect(jsonPath("$.data.events[0].title").value("Test Event 1"))
                .andExpect(jsonPath("$.data.events[0].location").value("Depok"))
                .andExpect(jsonPath("$.data.events[0].basePrice").value(100.0))
                .andExpect(jsonPath("$.data.events[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.data.events[1].title").value("Test Event 2"))
                .andExpect(jsonPath("$.data.events[1].location").value("Jakarta"))
                .andExpect(jsonPath("$.data.events[1].basePrice").value(200.0))
                .andExpect(jsonPath("$.data.events[1].status").value("PUBLISHED"));
    }

    @Test
    @WithMockUser(username = "12345678-1234-1234-1234-123456789012", authorities = "Organizer")
    void getMyEvents_EmptyList_ReturnsEmptyArray() throws Exception {

        when(eventService.listEventsByOrganizer(any(UUID.class))).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/events/organizer/my-events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.events").isArray())
                .andExpect(jsonPath("$.data.events.length()").value(0));
    }



}