package com.tech.scaffolding.asyncapis;

import com.tech.scaffolding.asyncapis.service.JokeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

        // Test the WebFlux implementation
        long startFlux = System.currentTimeMillis();
        log.info("Sending Web Flux Requests at - {}", LocalDateTime.now());

        // Kick off multiple, Webflux requests
        Mono<ResponseEntity<String>> monoResponse1 = jokeService.getRandomJoke_Flux();
        Mono<ResponseEntity<String>> monoResponse2 = jokeService.getRandomJoke_Flux();
        Mono<ResponseEntity<String>> monoResponse3 = jokeService.getRandomJoke_Flux();

        // Use Flux to handle multiple Mono instances
        /*
            doOnNext() is used for logging each response as it arrives.
            doOnComplete() is used to log the total elapsed time once all requests have been processed.
            blockLast() is called to block the execution until all requests are completed, with a timeout specified to avoid indefinite waiting.
                        This blocking is generally acceptable in a main method or a testing scenario but should be avoided in reactive
                        web applications to keep the non-blocking nature.
         */
        Flux.merge(monoResponse1, monoResponse2, monoResponse3)
                .doOnNext(response -> log.info("--> {}", response.getBody()))
                .doOnComplete(() -> log.info("Elapsed time for WebFlux requests: {} ms", System.currentTimeMillis() - startFlux))
                .blockLast(Duration.ofMinutes(1)); // Block until all requests are completed with a timeout
    }
}
