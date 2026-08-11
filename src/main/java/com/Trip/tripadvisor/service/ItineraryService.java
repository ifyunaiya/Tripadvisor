package com.Trip.tripadvisor.service;


import com.Trip.tripadvisor.dto.request.ItineraryRequest;
import com.Trip.tripadvisor.dto.response.DayPlan;
import com.Trip.tripadvisor.dto.response.ItineraryResponse;
import com.Trip.tripadvisor.dto.response.PexelsPhoto;
import com.Trip.tripadvisor.dto.response.TimeSlot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItineraryService {
    private final OpenAiService openAIService;
    private final PexelsService pexelsService;

    public ItineraryService(OpenAiService openAIService, PexelsService pexelsService) {

        this.openAIService = openAIService;
        this.pexelsService = pexelsService;
    }

    public ItineraryResponse getItinerary(ItineraryRequest request) {

        ItineraryResponse itinerary= openAIService.generateItinerary(request);

        for (DayPlan day : itinerary.getDays()) {

            addPhotos(day.getMorning());
            addPhotos(day.getAfternoon());
            addPhotos(day.getEvening());
        }
        return itinerary;
    }

    private void addPhotos(TimeSlot timeSlot) {

        if (timeSlot == null) {
            return;
        }

        String imageQuery = timeSlot.getImageQuery();

        if (imageQuery == null
                || imageQuery.isBlank()
                || imageQuery.equalsIgnoreCase("N/A")) {
            return;
        }

        System.out.println("Searching Pexels for: " + imageQuery);

        List<PexelsPhoto> photos =
                pexelsService.fetchPhotos(imageQuery);

        timeSlot.setPhotos(photos);
    }
}

//"imageQuery": "Paris hotel room"
//"imageQuery": "Le Marais food tour"
//"imageQuery": "Parisian bistro dinner"