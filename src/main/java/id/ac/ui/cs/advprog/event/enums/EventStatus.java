package id.ac.ui.cs.advprog.event.enums;

import lombok.Getter;

public enum EventStatus {
    DRAFT("DRAFT"),
    PUBLISHED("PUBLISHED"),
    CANCELLED("CANCELLED"),
    SOLD_OUT("SOLD_OUT"),
    COMPLETED("COMPLETED");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }
}