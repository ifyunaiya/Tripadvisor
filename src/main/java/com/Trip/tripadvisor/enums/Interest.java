package com.Trip.tripadvisor.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Interest {
    BEACHES("Beach activities, swimming, snorkeling, coastal relaxation"),
    HIKING("Trekking, mountain trails, nature walks, outdoor adventures"),
    CULTURE("Museums, historical sites, local customs, traditional performances"),
    FOOD("Local cuisine, food markets, cooking classes, restaurant experiences"),
    NIGHTLIFE("Bars, clubs, night markets, evening entertainment"),
    SHOPPING("Local markets, malls, artisan shops, souvenir hunting"),
    NATURE("Wildlife, national parks, botanical gardens, eco-tourism"),
    HISTORY("Ancient ruins, heritage sites, historical landmarks, guided tours"),
    ART("Galleries, street art, performances, creative workshops"),
    WELLNESS("Spas, yoga retreats, meditation, holistic experiences");

    private final String description;

    Interest(String description) {
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