package com.Trip.tripadvisor.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.Trip.tripadvisor.enums.Accommodation;
import com.Trip.tripadvisor.enums.Budget;
import com.Trip.tripadvisor.enums.Interest;
import com.Trip.tripadvisor.enums.TravelMode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItineraryResponse {

    // Request echo — frontend always knows what was planned
    private final String destination;
    private final int duration;
    private final int travelers;
    private final Budget budget;
    private final TravelMode mode;
    private final List<Interest> interests;
    private final Accommodation accommodation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate endDate;

    // AI-generated content
    private final String summary;
    private final List<String> highlights;
    private final List<DayPlan> days;
    private final PracticalInfo practicalInfo;
    private final String totalEstimatedBudget;

    // Destination hero images from Pexels
    private final List<PexelsPhoto> destinationPhotos;

    // Metadata
    private final long generationTimeMs;
}