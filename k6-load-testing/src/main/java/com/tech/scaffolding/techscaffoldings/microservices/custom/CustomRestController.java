package com.tech.scaffolding.techscaffoldings.microservices.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomRestController {

    @PostMapping(value = "/load-test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loadTest(@RequestBody String payload){
        log.info("CustomRestController:load-test :::: incoming payload {}", payload);
        return ResponseEntity.ok("success");
    }
}
