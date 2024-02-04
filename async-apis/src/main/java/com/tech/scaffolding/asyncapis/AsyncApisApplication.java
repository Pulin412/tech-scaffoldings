package com.tech.scaffolding.asyncapis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class AsyncApisApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncApisApplication.class, args);
	}

	@Bean
	public Executor taskExecutor() {
		// Can use newFixedThreadPool with number of threads as - 3 :
		// ExecutorService executor = Executors.newFixedThreadPool(3);

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("AsyncTest-");
		executor.initialize();
		return executor;
	}
}
