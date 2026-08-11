package com.Trip.tripadvisor.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TravelMode {
    RELAXED("Slow-paced, 1-2 activities per day, plenty of rest time"),
    MODERATE("Balanced pace, 3-4 activities per day, mix of activity and leisure"),
    ADVENTUROUS("Action-packed, 4-5 activities per day, high-energy experiences"),
    LUXURY("Premium experiences, private tours, exclusive venues, concierge-level planning");

    private final String description;

    TravelMode(String description) {
        this.description = description;
    }

    @JsonValue
    public String getValue() {
        return name();
    }

    public String getDescription() {
        return description;
    }
}