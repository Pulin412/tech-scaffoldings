package com.tech.scaffolding.asyncapis;

import com.tech.scaffolding.asyncapis.service.JokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AppRunner implements CommandLineRunner {

    private final JokeService jokeService;

    public AppRunner(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    @Override
    public void run(String... args) throws Exception {

        // Start the clock
        long start = System.currentTimeMillis();
        log.info("Sending Requests at - {}", LocalDateTime.now());

        // Kick off multiple, asynchronous requests
        CompletableFuture<ResponseEntity<String>> response1 = jokeService.getRandomJoke();
        CompletableFuture<ResponseEntity<String>> response2 = jokeService.getRandomJoke();
        CompletableFuture<ResponseEntity<String>> response3 = jokeService.getRandomJoke();

        // Wait until they are all done
        CompletableFuture.allOf(response1,response2,response3).join();

        // Print results, including elapsed time
        log.info("Elapsed time: " + (System.currentTimeMillis() - start));
        log.info("--> " + response1.get());
        log.info("--> " + response2.get());
        log.info("--> " + response3.get());
    }
}
