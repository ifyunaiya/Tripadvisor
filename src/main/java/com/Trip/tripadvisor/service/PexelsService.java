package com.Trip.tripadvisor.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.Trip.tripadvisor.dto.response.PexelsPhoto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PexelsService {

    private final WebClient pexelsWebClient;
    private final int imagesPerQuery;
    private final int timeoutSeconds;

    public PexelsService(
            @Qualifier("pexelsWebClient") WebClient pexelsWebClient,
            @Value("${pexels.images-per-day:3}") int imagesPerQuery,
            @Value("${pexels.timeout-seconds:10}") int timeoutSeconds) {
        this.pexelsWebClient = pexelsWebClient;
        this.imagesPerQuery = imagesPerQuery;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Fetches photos for a given search query. Returns empty list on any failure
     * so the API never fails due to image unavailability.
     */
    public List<PexelsPhoto> fetchPhotos(String query) {
        try {
            JsonNode response = pexelsWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("query", query)
                            .queryParam("per_page", imagesPerQuery)
                            .queryParam("orientation", "landscape")
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(ex -> {
                        log.warn("Pexels fetch failed for query '{}': {}", query, ex.getMessage());
                        return Mono.empty();
                    })
                    .block();

            if (response == null || !response.has("photos")) {
                log.warn("No photos returned for query: {}", query);
                return List.of();
            }

            List<PexelsPhoto> photos = new ArrayList<>();
            for (JsonNode photo : response.get("photos")) {
                photos.add(mapPhoto(photo));
            }
            return photos;

        } catch (Exception ex) {
            log.warn("Pexels service error for query '{}': {}", query, ex.getMessage());
            return List.of();
        }
    }

    /**
     * Fetches destination hero photos — uses a richer query for higher-quality
     * results.
     */
    public List<PexelsPhoto> fetchDestinationPhotos(String destination) {
        return fetchPhotos(destination + " travel landscape");
    }

    private PexelsPhoto mapPhoto(JsonNode node) {
        JsonNode src = node.path("src");
        return PexelsPhoto.builder()
                .id(node.path("id").asLong())
                .photographer(node.path("photographer").asText(null))
                .photographerUrl(node.path("photographer_url").asText(null))
                .originalUrl(src.path("original").asText(null))
                .largeUrl(src.path("large2x").asText(null))
                .mediumUrl(src.path("large").asText(null))
                .smallUrl(src.path("medium").asText(null))
                .alt(node.path("alt").asText(null))
                .build();
    }
}