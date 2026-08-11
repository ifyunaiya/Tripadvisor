package com.Trip.tripadvisor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PracticalInfo {
    private final String bestTimeToVisit;
    private final String currency;
    private final String language;
    private final String transportation;
    private final String emergencyContacts;
    private final List<String> packingTips;
}