package com.Trip.tripadvisor.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.Trip.tripadvisor.enums.Accommodation;
import com.Trip.tripadvisor.enums.Budget;
import com.Trip.tripadvisor.enums.Interest;
import com.Trip.tripadvisor.enums.TravelMode;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record ItineraryRequest(

        @NotBlank(message = "Destination is required") @Size(min = 2, max = 100, message = "Destination must be between 2 and 100 characters")
        String destination,

        @NotNull(message = "Duration is required") @Min(value = 1, message = "Duration must be at least 1 day") @Max(value = 30, message = "Duration cannot exceed 30 days")
        Integer duration,

        @NotNull(message = "Number of travelers is required") @Min(value = 1, message = "At least 1 traveler is required") @Max(value = 20, message = "Cannot exceed 20 travelers")
        Integer travelers,

        @NotNull(message = "Budget level is required")
        Budget budget,

        @NotNull(message = "Travel mode is required")
        TravelMode mode,

        @NotEmpty(message = "At least one interest is required") @Size(max = 5, message = "You can select up to 5 interests")
        List<Interest> interests,

        @NotNull(message = "Accommodation preference is required")
        Accommodation accommodation,

        @NotNull(message = "Start date is required") @Future(message = "Start date must be in the future") @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate) {
}