package id.ac.ui.cs.advprog.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.yaml.snakeyaml.events.Event;

import static org.assertj.core.api.Assertions.assertThat;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.EventBuilder;
@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

     private EventBuilder sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new Event();
        sampleEvent.setTitle("Test Event");
        sampleEvent.setDescription("Testing event creation");
        sampleEvent.setStatus(EventStatus.PUBLISHED);
        sampleEvent.setEventDate(LocalDateTime.now().plusDays(1));
        eventRepository.save(sampleEvent);
    }

    @Test
    void testAddEvent() {
        Event newEvent = new Event();
        newEvent.setTitle("Another Event");
        newEvent.setDescription("Description");
        newEvent.setStatus(EventStatus.ACTIVE);
        newEvent.setDateTime(LocalDateTime.now().plusDays(2));

        Event savedEvent = eventRepository.add(newEvent);
        assertThat(savedEvent.getId()).isNotNull();
    }

    @Test
    void testListEvents() {
        List<Event> events = eventRepository.listEvents();
        assertThat(events).isNotEmpty();
    }

    @Test
    void testGetById() {
        Optional<Event> found = eventRepository.getById(sampleEvent.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Event");
    }

    @Test
    void testUpdateEvent() {
        sampleEvent.setTitle("Updated Title");
        Event updated = eventRepository.updateEvent(sampleEvent);
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void testDeleteEvent() {
        UUID id = sampleEvent.getId();
        eventRepository.delete(id);
        Optional<Event> found = eventRepository.findById(id);
        assertThat(found).isNotPresent();
    }

    @Test
    void testUpdateStatus() {
        int updatedRows = eventRepository.updateStatus(sampleEvent.getId(), EventStatus.CANCELLED);
        assertThat(updatedRows).isEqualTo(1);

        Optional<Event> updatedEvent = eventRepository.findById(sampleEvent.getId());
        assertThat(updatedEvent).isPresent();
        assertThat(updatedEvent.get().getStatus()).isEqualTo(EventStatus.CANCELLED);
    }

}
