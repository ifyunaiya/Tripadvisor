package com.Trip.tripadvisor.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Budget {
    BUDGET("Budget-friendly, hostels and street food, under $50/day"),
    MODERATE("Mid-range, 3-star hotels and local restaurants, $50-$150/day"),
    LUXURY("Luxury, 5-star hotels and fine dining, $150+/day");

    private final String description;

    Budget(String description) {
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