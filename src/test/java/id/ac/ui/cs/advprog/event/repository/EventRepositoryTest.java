package id.ac.ui.cs.advprog.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.yaml.snakeyaml.events.Event; // Removed as it conflicts with id.ac.ui.cs.advprog.event.model.Event

import static org.assertj.core.api.Assertions.assertThat;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event;
import id.ac.ui.cs.advprog.event.model.EventBuilder;
@DataJpaTest
public class EventRepositoryTest {


    @Autowired
    private EventRepository eventRepository;

    private UUID eventId;
    private LocalDateTime eventDate;
    private Event testEvent; 

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        eventDate = LocalDateTime.now().plusMonths(4);
        UUID userId = UUID.randomUUID();
        testEvent = new Event();
        testEvent.setId(eventId);
        testEvent.setTitle("Pemrograman Lanjut");
        testEvent.setDescription("Design Pattern");
        testEvent.setEventDate(eventDate);
        testEvent.setLocation("Depok");
        testEvent.setBasePrice(100.0);
        testEvent.setStatus(EventStatus.DRAFT);
        testEvent.setUserId(userId);
    }


   @Test
  void testFindByEventDate() {
       LocalDateTime date = LocalDateTime.of(2025, 5, 10, 10, 0);

       Event event = new EventBuilder()
           .setTitle("Event 1")
           .setDescription("Description")
           .setEventDate(date)
           .setLocation("Jakarta")
           .setBasePrice(0.0)
           .build();
           
       eventRepository.save(event);

       List<Event> result = eventRepository.findByEventDate(date);
       assertThat(result).hasSize(1);
       assertThat(result.get(0).getTitle()).isEqualTo("Event 1");
   }

   @Test
   void testFindByLocation() {
        Event eventForLocationTest = new Event();
        eventForLocationTest.setTitle("Pemrograman Lanjut");
        eventForLocationTest.setDescription("Design Pattern");
        eventForLocationTest.setEventDate(LocalDateTime.now().plusMonths(4));
        eventForLocationTest.setLocation("Depok");
        eventForLocationTest.setBasePrice(100.0);
        eventForLocationTest.setStatus(EventStatus.DRAFT);
        eventRepository.save(eventForLocationTest);

       List<Event> result = eventRepository.findByLocation("Depok");
       assertThat(result).hasSize(1);
       assertThat(result.get(0).getLocation()).isEqualTo("Depok");
   }

   @Test
   void testFindByEventDateAfter() {
         LocalDateTime now = LocalDateTime.now();
         Event eventDateAfter = new Event();
        eventDateAfter.setTitle("Pemrograman Lanjut");
        eventDateAfter.setDescription("Design Pattern");
        eventDateAfter.setEventDate(LocalDateTime.now().plusMonths(4));
        eventDateAfter.setLocation("Depok");
        eventDateAfter.setBasePrice(100.0);
        eventDateAfter.setStatus(EventStatus.DRAFT);
        eventRepository.save(eventDateAfter);
      

       List<Event> result = eventRepository.findByEventDateAfter(now);
       assertThat(result).isNotEmpty();
       assertThat(result.get(0).getTitle()).isEqualTo("Pemrograman Lanjut");
   }

}
