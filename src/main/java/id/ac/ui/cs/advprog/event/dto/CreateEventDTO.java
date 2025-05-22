package id.ac.ui.cs.advprog.event.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.ac.ui.cs.advprog.event.enums.EventStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateEventDTO {



    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("event_date")
    private LocalDateTime eventDate;

    @JsonProperty("location")
    private String location;

    @JsonProperty("basePrice")
    private double basePrice;



    @JsonProperty("user_id")
    private UUID userId;



}
