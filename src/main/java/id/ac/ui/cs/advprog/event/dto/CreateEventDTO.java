package id.ac.ui.cs.advprog.event.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateEventDTO {



    @JsonProperty("title")
    @NotBlank(message = "Event title cannot be null or empty")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("event_date")
    @NotNull(message = "Event date cannot be null")
    private LocalDateTime eventDate;

    @JsonProperty("location")
    @NotBlank(message = "Event location cannot be null or empty")
    private String location;

    @JsonProperty("basePrice")
    private double basePrice;

    @JsonProperty("user_id")
    private UUID userId;



}
