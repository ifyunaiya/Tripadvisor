package com.Trip.tripadvisor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItineraryResponse {

    private final String destination;
    private final String summary;
    private final List<String> highlights;
    private final List<DayPlan> days;
    private final PracticalInfo practicalInfo;
    private final String totalEstimatedBudget;
}