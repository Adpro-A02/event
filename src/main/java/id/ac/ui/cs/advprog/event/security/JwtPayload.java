package id.ac.ui.cs.advprog.event.security;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class JwtPayload {
    // Getters & setters
    private UUID sub;       // userId
    private String role;
    private Long exp;

}
