package com.Trip.tripadvisor.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DayPlan {
    private final int dayNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date;

    private final String theme;
    private final TimeSlot morning;
    private final TimeSlot afternoon;
    private final TimeSlot evening;
    private final AccommodationSuggestion accommodation;
    private final String dailyBudgetEstimate;
}