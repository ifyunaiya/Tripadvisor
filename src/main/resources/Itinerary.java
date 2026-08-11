package com.Trip.tripadvisor.service;

import com.Trip.tripadvisor.dto.request.ItineraryRequest;
import com.Trip.tripadvisor.dto.response.ItineraryResponse;
import org.springframework.stereotype.Service;

@Service
public class TripAdvisor {
    ItineraryRequest itineraryRequest;
    ItineraryResponse itineraryResponse;

    public ItineraryResponse RequestItenary(ItineraryRequest itineraryRequest){
        this.itineraryRequest = itineraryRequest;
        return itineraryResponse;
        //Call Open API with the request object and set up a response object to de associate the api response

    }
}
