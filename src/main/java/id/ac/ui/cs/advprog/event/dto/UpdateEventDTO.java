package id.ac.ui.cs.advprog.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventDTO {
    
   

    @NotBlank(message = "Title tidak boleh kosong")
    private String title;



    private String description;

    @JsonProperty("event_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Future(message = "Event date harus di masa depan")
    private LocalDateTime eventDate;

    private String location;

    private double basePrice;

    private EventStatus status;
}

