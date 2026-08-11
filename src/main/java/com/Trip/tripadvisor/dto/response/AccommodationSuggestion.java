package com.Trip.tripadvisor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccommodationSuggestion {
    private final String suggestion;
    private final String estimatedCost;
}