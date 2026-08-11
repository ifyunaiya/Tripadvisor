package com.Trip.tripadvisor.config;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class OpenAiConfig {

        @Value("${openai.api-key}")
        private String apiKey;

        @Value("${openai.base-url}")
        private String baseUrl;

        @Bean("openAiWebClient")
        public WebClient openAiWebClient() {
                HttpClient httpClient = HttpClient.create()
                                .resolver(DefaultAddressResolverGroup.INSTANCE);

                ExchangeStrategies strategies = ExchangeStrategies.builder()
                                .codecs(config -> config.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                                .build();

                return WebClient.builder()
                                .baseUrl(baseUrl)
                                .clientConnector(new ReactorClientHttpConnector(httpClient))
                                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .exchangeStrategies(strategies)
                                .build();
        }

}