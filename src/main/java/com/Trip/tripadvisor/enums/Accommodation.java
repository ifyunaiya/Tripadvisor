package com.Trip.tripadvisor.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Accommodation {
    HOSTEL("Budget dormitory or private rooms in hostels, social atmosphere"),
    HOTEL("Standard hotel with amenities, room service, central locations"),
    RESORT("All-inclusive or premium resorts with pools, spas, and facilities"),
    AIRBNB("Private apartments or homes, local neighborhood experience"),
    CAMPING("Tents, glamping, or campsite accommodations, close to nature");

    private final String description;

    Accommodation(String description) {
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