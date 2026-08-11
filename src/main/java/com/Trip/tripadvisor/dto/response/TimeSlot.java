package com.Trip.tripadvisor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeSlot {
    private final String title;
    private final String description;
    private final String location;
    private final String estimatedDuration;
    private final String estimatedCost;
    private final String tips;
    private final String imageQuery;
    @Setter
    private List<PexelsPhoto> photos;

}