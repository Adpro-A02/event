package id.ac.ui.cs.advprog.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import id.ac.ui.cs.advprog.event.enums.EventStatus;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventDTO {
    
    private UUID id;

    @NotBlank(message = "Title tidak boleh kosong")
    private String title;



    private String description;

    
    @Future(message = "Event date harus di masa depan")
    private LocalDateTime eventDate;

    private String location;

    private double basePrice;

    private EventStatus status;
}

