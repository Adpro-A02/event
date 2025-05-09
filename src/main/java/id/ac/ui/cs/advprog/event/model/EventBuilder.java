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

  
    public EventBuilder() {}

    public EventBuilder(String title, String description, LocalDateTime eventDate, String location, double basePrice) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.basePrice = basePrice;
        this.status = EventStatus.DRAFT;
    }

    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}