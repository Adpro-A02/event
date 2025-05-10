package id.ac.ui.cs.advprog.event.model;

import java.time.LocalDateTime;
import java.util.UUID;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
import lombok.Getter;

@Getter
public class EventBuilder {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private double basePrice;
    private EventStatus status = EventStatus.DRAFT;

    

    public EventBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public EventBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public EventBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EventBuilder setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public EventBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public EventBuilder setBasePrice(double basePrice) {
        this.basePrice = basePrice;
        return this;
    }

    public EventBuilder setStatus(EventStatus status) {
        this.status = status;
        return this;
    }

    public Event build() {
        return new Event(this);
    }
}
