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

    @BeforeEach
    void setUp() {
        // Clear the repository before each test to avoid test interference
        eventRepository.deleteAll();

        // Create a test user ID to use in all tests
        testUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should find events by exact event date")
    void testFindByEventDate() {
        // Given
        LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);

        Event event = new EventBuilder()
                .setTitle("Event 1")
                .setDescription("Description")
                .setEventDate(date)
                .setLocation("Jakarta")
                .setBasePrice(0.0)
                .setUserId(testUserId) // Set the user ID
                .build();

        eventRepository.save(event);

        // When
        List<Event> result = eventRepository.findByEventDate(date);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Event 1");
        assertThat(result.get(0).getEventDate()).isEqualTo(date);
    }

    @Test
    @DisplayName("Should find events by location")
    void testFindByLocation() {
        // Given
        Event event = new EventBuilder()
                .setTitle("Pemrograman Lanjut")
                .setDescription("Design Pattern")
                .setEventDate(LocalDateTime.now().plusMonths(4))
                .setLocation("Depok")
                .setBasePrice(100.0)
                .setUserId(testUserId) // Set the user ID
                .build();

        eventRepository.save(event);

        // When
        List<Event> result = eventRepository.findByLocation("Depok");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocation()).isEqualTo("Depok");
        assertThat(result.get(0).getTitle()).isEqualTo("Pemrograman Lanjut");
    }

    @Test
    @DisplayName("Should find events with dates after a specific date")
    void testFindByEventDateAfter() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusMonths(4);

        Event event = new EventBuilder()
                .setTitle("Pemrograman Lanjut")
                .setDescription("Design Pattern")
                .setEventDate(futureDate)
                .setLocation("Depok")
                .setBasePrice(100.0)
                .setUserId(testUserId) // Set the user ID
                .build();

        eventRepository.save(event);

        // Also add a past event to verify it's not returned
        Event pastEvent = new EventBuilder()
                .setTitle("Past Event")
                .setDescription("This happened already")
                .setEventDate(now.minusMonths(1))
                .setLocation("Jakarta")
                .setBasePrice(50.0)
                .setUserId(testUserId) // Set the user ID
                .build();

        eventRepository.save(pastEvent);

        // When
        List<Event> result = eventRepository.findByEventDateAfter(now);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Pemrograman Lanjut");
        assertThat(result.get(0).getEventDate()).isAfter(now);
    }

    @Test
    @DisplayName("Should return empty list when no events match the criteria")
    void testNoMatchingEvents() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusYears(10);

        // When
        List<Event> dateResults = eventRepository.findByEventDate(futureDate);
        List<Event> locationResults = eventRepository.findByLocation("NonExistentLocation");

        // Then
        assertThat(dateResults).isEmpty();
        assertThat(locationResults).isEmpty();
    }
}