package com.Trip.tripadvisor.controller;

import com.Trip.tripadvisor.dto.request.ItineraryRequest;
import com.Trip.tripadvisor.dto.response.ApiResponse;
import com.Trip.tripadvisor.dto.response.ItineraryResponse;
import com.Trip.tripadvisor.dto.response.PexelsPhoto;
import com.Trip.tripadvisor.service.ItineraryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TripAdvisor {
    private final ItineraryService itineraryService;

    public TripAdvisor(ItineraryService itineraryService, ObjectMapper objectMapper) {
        this.itineraryService = itineraryService;
    }

    @PostMapping("/post_itinerary")
    public ApiResponse<ItineraryResponse> makeRequest(@Valid @RequestBody ItineraryRequest itineraryRequest) {

        ItineraryResponse itinerary = itineraryService.getItinerary(itineraryRequest);
        return ApiResponse.ok(itinerary);
    }


}
