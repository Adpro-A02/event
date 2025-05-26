package id.ac.ui.cs.advprog.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.model.EventBuilder;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    private UUID testUserId;
    private UUID userId1;
    private UUID userId2;
    private LocalDateTime eventDate;
    @BeforeEach
    void setUp() {

        eventRepository.deleteAll();
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusDays(7);
        testUserId = UUID.randomUUID();

        Event event1 = new Event();

        event1.setTitle("Event 1");
        event1.setDescription("Description 1");
        event1.setEventDate(eventDate);
        event1.setLocation("Location 1");
        event1.setBasePrice(50.0);
        event1.setStatus(EventStatus.PUBLISHED);
        event1.setUserId(userId1);

        Event event2 = new Event();

        event2.setTitle("Event 2");
        event2.setDescription("Description 2");
        event2.setEventDate(eventDate.minusDays(1));
        event2.setLocation("Location 2");
        event2.setBasePrice(70.0);
        event2.setStatus(EventStatus.COMPLETED);
        event2.setUserId(userId1);

        Event event3 = new Event();

        event3.setTitle("Event 3");
        event3.setDescription("Description 3");
        event3.setEventDate(eventDate.plusDays(3));
        event3.setLocation("Location 3");
        event3.setBasePrice(80.0);
        event3.setStatus(EventStatus.DRAFT);
        event3.setUserId(userId2);

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

    }

    @Test
    @DisplayName("Should find events by exact event date")
    void testFindByEventDate() {

        LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);

        Event event = new EventBuilder()
                .setTitle("Event 1")
                .setDescription("Description")
                .setEventDate(date)
                .setLocation("Jakarta")
                .setBasePrice(0.0)
                .setUserId(testUserId)
                .build();

        eventRepository.save(event);

        List<Event> result = eventRepository.findByEventDate(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Event 1");
        assertThat(result.get(0).getEventDate()).isEqualTo(date);
    }

    @Test
    @DisplayName("Should find events by location")
    void testFindByLocation() {

        Event event = new EventBuilder()
                .setTitle("Pemrograman Lanjut")
                .setDescription("Design Pattern")
                .setEventDate(LocalDateTime.now().plusMonths(4))
                .setLocation("Depok")
                .setBasePrice(100.0)
                .setUserId(testUserId)
                .build();

        eventRepository.save(event);

        List<Event> result = eventRepository.findByLocation("Depok");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocation()).isEqualTo("Depok");
        assertThat(result.get(0).getTitle()).isEqualTo("Pemrograman Lanjut");
    }

    @Test
    @DisplayName("Should find events with dates after a specific date")
    void testFindByEventDateAfter() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusMonths(4);

        Event event = new EventBuilder()
                .setTitle("Pemrograman Lanjut")
                .setDescription("Design Pattern")
                .setEventDate(futureDate)
                .setLocation("Depok")
                .setBasePrice(100.0)
                .setUserId(testUserId)
                .build();

        eventRepository.save(event);

        Event pastEvent = new EventBuilder()
                .setTitle("Past Event")
                .setDescription("This happened already")
                .setEventDate(now.minusMonths(1))
                .setLocation("Jakarta")
                .setBasePrice(50.0)
                .setUserId(testUserId)
                .build();

        eventRepository.save(pastEvent);

        List<Event> result = eventRepository.findByEventDateAfter(now);

        assertThat(result).hasSize(4);
        assertThat(result.get(0).getTitle()).isEqualTo("Event 1");
        assertThat(result.get(0).getEventDate()).isAfter(now);
    }

    @Test
    @DisplayName("Should return empty list when no events match the criteria")
    void testNoMatchingEvents() {

        LocalDateTime futureDate = LocalDateTime.now().plusYears(10);

        List<Event> dateResults = eventRepository.findByEventDate(futureDate);
        List<Event> locationResults = eventRepository.findByLocation("NonExistentLocation");

        assertThat(dateResults).isEmpty();
        assertThat(locationResults).isEmpty();
    }

    @Test
    void testFindByStatusIn_returnsCorrectEvents() {
        List<EventStatus> statuses = List.of(EventStatus.PUBLISHED, EventStatus.COMPLETED);

        List<Event> results = eventRepository.findByStatusIn(statuses);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Event::getStatus).containsOnly(EventStatus.PUBLISHED, EventStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should return empty list when no events found for user")
    void testFindByUserId_ShouldReturnEmptyListWhenNoEventsFound() {
        UUID nonExistentUserId = UUID.randomUUID();
        List<Event> events = eventRepository.findByUserId(nonExistentUserId);
        assertThat(events).isEmpty();
    }

    @Test
    @DisplayName("Should return events with different statuses for same user")
    void testFindByUserId_ShouldReturnEventsWithDifferentStatuses() {
        List<Event> userId1Events = eventRepository.findByUserId(userId1);

        assertThat(userId1Events).hasSize(2);
        assertThat(userId1Events).extracting(Event::getStatus)
                .containsExactlyInAnyOrder(EventStatus.PUBLISHED, EventStatus.COMPLETED);
    }
}
