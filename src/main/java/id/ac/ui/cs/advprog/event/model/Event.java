package id.ac.ui.cs.advprog.event.model;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JsonProperty("event_date") // ini agar nama JSON "event_date" bisa masuk ke field ini
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "base_price", nullable = false)
    private double basePrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.DRAFT;

 
    public Event(EventBuilder builder) {
        this.id = builder.getId();
        this.title = builder.getTitle();
        this.description = builder.getDescription();
        this.eventDate = builder.getEventDate();
        this.location = builder.getLocation();
        this.basePrice = builder.getBasePrice();
        this.status = EventStatus.DRAFT;
    }
}
