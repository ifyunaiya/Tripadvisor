package com.Trip.tripadvisor.service;

import com.Trip.tripadvisor.dto.response.ItineraryResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Trip.tripadvisor.dto.request.ItineraryRequest;
import com.Trip.tripadvisor.exception.ItineraryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OpenAiService {

    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final int maxTokens;
    private final double temperature;
    private final int timeoutSeconds;
    private final String systemPrompt;

    public OpenAiService(
            @Qualifier("openAiWebClient") WebClient openAiWebClient,
            ObjectMapper objectMapper,
            @Value("${openai.model:gpt-4o}") String model,
            @Value("${openai.max-tokens:4096}") int maxTokens,
            @Value("${openai.temperature:0.7}") double temperature,
            @Value("${openai.timeout-seconds:60}") int timeoutSeconds) {
        this.openAiWebClient = openAiWebClient;
        this.objectMapper = objectMapper;
        this.model = model;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.timeoutSeconds = timeoutSeconds;
        this.systemPrompt = loadSystemPrompt();
    }

    public ItineraryResponse generateItinerary(ItineraryRequest request) {
        String userPrompt = buildUserPrompt(request);
        log.info("Calling OpenAI [model={}] for destination: {}", model, request.destination());
        log.debug("System prompt length: {} chars", systemPrompt.length());
        log.debug("User prompt:\n{}", userPrompt);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "temperature", temperature,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)));

        try {
            JsonNode response = openAiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (response == null) {
                throw new ItineraryException(
                        "OpenAI returned an empty response",
                        "AI_EMPTY_RESPONSE",
                        HttpStatus.BAD_GATEWAY);
            }

            String content = extractContent(response);
            log.debug("OpenAI responded — parsing JSON content ({} chars)", content.length());
            return objectMapper.readValue(content, ItineraryResponse.class);

        } catch (ItineraryException ex) {
            // Already typed — rethrow as-is
            throw ex;
        } catch (Exception ex) {
            Throwable cause = unwrapReactorException(ex);

            if (cause instanceof WebClientResponseException wcEx) {
                String body = wcEx.getResponseBodyAsString();
                log.error("OpenAI HTTP error [status={}]: {}", wcEx.getStatusCode(), body);
                throw new ItineraryException(
                        "OpenAI API error " + wcEx.getStatusCode() + ": " + extractOpenAiErrorMessage(body),
                        "AI_API_ERROR",
                        HttpStatus.BAD_GATEWAY,
                        wcEx);
            }

            if (cause instanceof IOException ioEx) {
                log.error("Failed to parse OpenAI JSON response", ioEx);
                throw new ItineraryException(
                        "Failed to parse AI-generated itinerary",
                        "AI_PARSE_ERROR",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ioEx);
            }

            if (cause instanceof java.util.concurrent.TimeoutException timeoutEx) {
                log.error("OpenAI request timed out after {}s", timeoutSeconds);
                throw new ItineraryException(
                        "AI service timed out. Try a shorter trip duration.",
                        "AI_TIMEOUT",
                        HttpStatus.GATEWAY_TIMEOUT,
                        timeoutEx);
            }

            log.error("Unexpected error calling OpenAI [{}]: {}", cause.getClass().getSimpleName(), cause.getMessage(),
                    cause);
            throw new ItineraryException(
                    "AI service is temporarily unavailable: " + cause.getMessage(),
                    "AI_UNAVAILABLE",
                    HttpStatus.SERVICE_UNAVAILABLE,
                    cause);
        }
    }

    private String extractContent(JsonNode response) {
        JsonNode choices = response.path("choices");
        if (choices.isMissingNode() || choices.isEmpty()) {
            log.error("OpenAI response missing choices: {}", response);
            throw new ItineraryException(
                    "OpenAI returned no choices in response",
                    "AI_NO_CHOICES",
                    HttpStatus.BAD_GATEWAY);
        }
        String content = choices.get(0).path("message").path("content").asText("").trim();
        if (content.isBlank()) {
            log.error("OpenAI returned blank content. Full response: {}", response);
            throw new ItineraryException(
                    "OpenAI returned blank content",
                    "AI_BLANK_CONTENT",
                    HttpStatus.BAD_GATEWAY);
        }
        return content;
    }

    /**
     * Reactor's block() wraps exceptions in ReactiveException. Unwrap to get the
     * real cause.
     */
    private Throwable unwrapReactorException(Throwable ex) {
        Throwable cause = ex.getCause();
        if (cause != null && ex.getClass().getName().contains("ReactiveException")) {
            return cause;
        }
        // Also handle direct wrapping by reactor
        if (cause instanceof WebClientResponseException
                || cause instanceof IOException
                || cause instanceof java.util.concurrent.TimeoutException) {
            return cause;
        }
        return ex;
    }

    /**
     * Tries to extract the human-readable error message from OpenAI's error JSON
     * body.
     * Falls back to the raw body string if parsing fails.
     */
    private String extractOpenAiErrorMessage(String responseBody) {
        try {
            JsonNode node = objectMapper.readTree(responseBody);
            String msg = node.path("error").path("message").asText("");
            return msg.isBlank() ? responseBody : msg;
        } catch (Exception ex) {
            return responseBody;
        }
    }

    private String buildUserPrompt(ItineraryRequest request) {
        String interests = request.interests().stream()
                .map(i -> i.name() + " (" + i.getDescription() + ")")
                .collect(Collectors.joining(", "));

        return """
                Plan a %d-day trip to %s for %d traveler(s).

                PREFERENCES:
                - Travel Style: %s — %s
                - Budget: %s — %s
                - Interests: %s
                - Accommodation: %s — %s
                - Start Date: %s

                Generate a detailed day-by-day itinerary following exactly the JSON schema in your instructions.
                Ensure activities align with the %s travel style and %s budget level.
                Prioritize experiences matching: %s.
                Recommend %s-type accommodation options throughout.
                """.formatted(
                request.duration(),
                request.destination(),
                request.travelers(),
                request.mode().name(), request.mode().getDescription(),
                request.budget().name(), request.budget().getDescription(),
                interests,
                request.accommodation().name(), request.accommodation().getDescription(),
                request.startDate(),
                request.mode().name(),
                request.budget().name(),
                request.interests().stream().map(Enum::name).collect(Collectors.joining(", ")),
                request.accommodation().name());
    }

    /**
     * Loads system prompt from classpath using InputStream — compatible with all
     * Spring/JDK versions
     * and works correctly inside a JAR (unlike File-based approaches).
     */
    private String loadSystemPrompt() {
        String path = "prompts/itinerary-system.txt";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException(
                        "System prompt not found at classpath:" + path +
                                ". Ensure src/main/resources/prompts/itinerary-system.txt exists.");
            }
            String prompt = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            log.info("System prompt loaded ({} chars)", prompt.length());
            return prompt;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read system prompt from classpath:" + path, ex);
        }
    }
}