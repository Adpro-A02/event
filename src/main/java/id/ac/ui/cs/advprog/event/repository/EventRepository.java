package id.ac.ui.cs.advprog.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
import id.ac.ui.cs.advprog.event.model.Event; 

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByEventDate(LocalDateTime eventDate);
    List<Event> findByLocation(String location);
    List<Event> findByEventDateAfter(LocalDateTime now);
    @Query("SELECT e FROM Event e WHERE e.userId = :userId OR e.status IN :status")
    List<Event> findOwnOrPublishedEvents(@Param("userId") UUID userId, @Param("status") List<EventStatus> statuses);
    List<Event> findByStatus(EventStatus status);
    @Query("SELECT e FROM Event e WHERE e.status IN :status")
    List<Event> findByStatusIn(@Param("status") List<EventStatus> status);
    List<Event> findByUserId(UUID userId);
   
}