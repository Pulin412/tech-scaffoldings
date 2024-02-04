package com.tech.scaffolding.asyncapis.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class JokeService {

    private final RestTemplate restTemplate;

    private final WebClient webClient;

    public JokeService(RestTemplateBuilder restTemplateBuilder, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.webClient = webClientBuilder.baseUrl("https://icanhazdadjoke.com/").build();
    }

    @Async
    public CompletableFuture<ResponseEntity<String>> getRandomJoke() throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        log.info("Fetching a joke....current thread - {}", Thread.currentThread().getName());
        Thread.sleep(3000L);
        return CompletableFuture.completedFuture(
                restTemplate.exchange("https://icanhazdadjoke.com/", HttpMethod.GET, requestEntity, String.class, Collections.EMPTY_MAP));
    }

    public Mono<ResponseEntity<String>> getRandomJoke_Flux() {
        return webClient.get()
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .retrieve()
                .toEntity(String.class)
                .delaySubscription(Duration.ofMillis(3000L))
                .doOnSubscribe(subscription -> log.info("Fetching a joke....current thread - {}", Thread.currentThread().getName()));
    }

    public Flux<String> getRandomJokes_Flux(int numOfJokes) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("limit", numOfJokes)
                        .build())
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .retrieve()
                .bodyToMono(JsonNode.class)
                .delaySubscription(Duration.ofMillis(3000L))
                .flatMapMany(jsonNode -> {
                    List<String> jokes = jsonNode.path("results")   // manipulated as per https://icanhazdadjoke.com/api
                            .findValuesAsText("joke");
                    return Flux.fromIterable(jokes);
                });
    }
}
