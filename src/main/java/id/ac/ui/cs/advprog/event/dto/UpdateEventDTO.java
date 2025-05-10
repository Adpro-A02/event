package id.ac.ui.cs.advprog.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;


import java.time.LocalDateTime;

import id.ac.ui.cs.advprog.event.enums.EventStatus;

public class UpdateEventDTO {

    @NotBlank(message = "Title tidak boleh kosong")
    private String title;

    private String description;

    
    @Future(message = "Event date harus di masa depan")
    private LocalDateTime eventDate;

    private String location;

    private double basePrice;

    private EventStatus status;
}

