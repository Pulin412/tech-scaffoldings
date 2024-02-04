package com.tech.scaffolding.asyncapis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class JokeService {

    private final RestTemplate restTemplate;

    public JokeService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
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
}
