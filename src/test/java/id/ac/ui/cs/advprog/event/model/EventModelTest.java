package id.ac.ui.cs.advprog.event.model;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.event.enums.EventStatus;

class EventModelTest {

    private EventBuilder event;
    private final String title = "Concert";
    private final String description = "Music concert";
    private final LocalDateTime eventDate = LocalDateTime.of(2025, 6, 15, 19, 30);
    private final String location = "Jakarta Concert Hall";
    private final double basePrice = 500000.0;

    @BeforeEach
    void setUp() {
        event = new EventBuilder();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(event, "Event should not be null");
        assertEquals(EventStatus.DRAFT, event.getStatus(), "Default status should be DRAFT");
    }

//    @Test
//    void testParameterizedConstructor() {
//        EventBuilder paramEvent = new EventBuilder(title, description, eventDate, location, basePrice);
//
//        assertEquals(title, paramEvent.getTitle(), "Title should match");
//        assertEquals(description, paramEvent.getDescription(), "Description should match");
//        assertEquals(eventDate, paramEvent.getEventDate(), "Event date should match");
//        assertEquals(location, paramEvent.getLocation(), "Location should match");
//        assertEquals(basePrice, paramEvent.getBasePrice(), "Base price should match");
//        assertEquals(EventStatus.DRAFT, paramEvent.getStatus(), "Status should be DRAFT");
//    }

    @Test
    void testSetAndGetId() {
        UUID id = UUID.randomUUID();
        event.setId(id);
        assertEquals(id, event.getId(), "ID should match");
    }

    @Test
    void testSetAndGetTitle() {
        event.setTitle(title);
        assertEquals(title, event.getTitle(), "Title should match");
    }

    @Test
    void testSetAndGetDescription() {
        event.setDescription(description);
        assertEquals(description, event.getDescription(), "Description should match");
    }

    @Test
    void testSetAndGetEventDate() {
        event.setEventDate(eventDate);
        assertEquals(eventDate, event.getEventDate(), "Event date should match");
    }

    @Test
    void testSetAndGetLocation() {
        event.setLocation(location);
        assertEquals(location, event.getLocation(), "Location should match");
    }

    @Test
    void testSetAndGetBasePrice() {
        event.setBasePrice(basePrice);
        assertEquals(basePrice, event.getBasePrice(), "Base price should match");
    }

    @Test
    void testSetAndGetStatus() {
        event.setStatus(EventStatus.PUBLISHED);
        assertEquals(EventStatus.PUBLISHED, event.getStatus(), "Status should match");

        event.setStatus(EventStatus.CANCELLED);
        assertEquals(EventStatus.CANCELLED, event.getStatus(), "Status should match after update");
    }
}
