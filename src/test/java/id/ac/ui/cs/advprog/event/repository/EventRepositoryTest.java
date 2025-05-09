package id.ac.ui.cs.advprog.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private EventRepository eventBuilderRepository;

    @Test
    @DisplayName("Test findByEventDate")
    void testFindByEventDate() {
        LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);

        EventBuilder event = new EventBuilder("Event 1", "Description", date, "Jakarta", 0.0);
        eventBuilderRepository.save(event);

        List<EventBuilder> result = eventBuilderRepository.findByEventDate(date);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventName()).isEqualTo("Event 1");
    }

    @Test
    @DisplayName("Test findByLocation")
    void testFindByLocation() {
        EventBuilder event = new EventBuilder("Event 2", LocalDateTime.now(), "Bandung");
        eventBuilderRepository.save(event);

        List<EventBuilder> result = eventBuilderRepository.findByLocation("Bandung");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocation()).isEqualTo("Bandung");
    }

    @Test
    @DisplayName("Test findByEventDateAfter")
    void testFindByEventDateAfter() {
        LocalDateTime now = LocalDateTime.now();
        EventBuilder futureEvent = new EventBuilder("Future Event", now.plusDays(1), "Surabaya");
        eventBuilderRepository.save(futureEvent);

        List<EventBuilder> result = eventBuilderRepository.findByEventDateAfter(now);
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getEventName()).isEqualTo("Future Event");
    }

}
