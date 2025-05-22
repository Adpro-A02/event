package id.ac.ui.cs.advprog.event.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private Instant timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }

}

