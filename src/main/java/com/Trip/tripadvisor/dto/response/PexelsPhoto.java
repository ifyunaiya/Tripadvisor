package com.Trip.tripadvisor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PexelsPhoto {
        private final long id;
        private final String photographer;
        private final String photographerUrl;
        private final String originalUrl;
        private final String largeUrl;
        private final String mediumUrl;
        private final String smallUrl;
        private final String alt;
}