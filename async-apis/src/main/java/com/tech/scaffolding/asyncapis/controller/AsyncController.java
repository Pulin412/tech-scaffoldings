package com.tech.scaffolding.asyncapis.controller;

import com.tech.scaffolding.asyncapis.service.JokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class AsyncController {

    private final JokeService jokeService;

    public AsyncController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    @GetMapping(value = "/getAsyncJoke", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<String>> getJoke() throws InterruptedException {
        log.info("Async HTTP Request Thread -- {}", Thread.currentThread().getName());
        return jokeService.getRandomJoke();
    }
}
