package com.umograd.content.infrastructure.external;

import com.umograd.content.domain.external.ContentProvider;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class HttpContentProvider implements ContentProvider {

    private final WebClient webClient;
    private final String baseUrl;

    public HttpContentProvider(WebClient webClient, String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<ExternalTask> fetchTasks(String topic, int limit) {
        // Example: GET {baseUrl}/tasks?topic=math&limit=10
        record ExternalTaskDto(
                String title,
                String description,
                int minAge,
                int maxAge,
                String difficulty
        ) {}

        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tasks")
                        .queryParam("topic", topic)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToFlux(ExternalTaskDto.class)
                .collectList()
                .block();

        return response.stream()
                .map(dto -> new ExternalTask(
                        dto.title(),
                        dto.description(),
                        dto.minAge(),
                        dto.maxAge(),
                        dto.difficulty()
                ))
                .toList();
    }
}
