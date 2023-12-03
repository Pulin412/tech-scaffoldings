# tech-scaffoldings

## Overview

Ready to use code templates in Java.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Database Setup](#database-setup)
3. [REST API Endpoint](#rest-api-endpoint)
4. [Security Configuration](#security-configuration)
5. [Asynchronous Processing](#asynchronous-processing)
6. [Caching](#caching)
7. [Exception Handling](#exception-handling)
8. [Logging Setup](#logging-setup)
9. [Testing](#testing)
10. [Swagger Documentation](#swagger-documentation)
11. [Continuous Integration](#continuous-integration)
12. [Monitoring and Metrics](#monitoring-and-metrics)
13. [Dockerization](#dockerization)
14. [Load Testing](#load-testing)
15. [Dashboarding](#dashboarding)

## Getting Started

Instructions on how to clone, build, and run the project locally.

## REST API Endpoint

Example REST controller with basic CRUD operations and input validation.

## Database Setup

Details on connecting to a database and performing basic CRUD operations.

## Security Configuration

Instructions on securing the Spring Boot application, including JWT token authentication.

## Asynchronous Processing

Example of using Spring's @Async for asynchronous processing and integration with a message queue.

## Caching

Integration with Spring Cache for caching and an example of caching a method result.

## Exception Handling

Global exception handling setup and custom exception classes.

## Logging Setup

Configuration for logging using SLF4J and Logback, with different log levels.

## Testing

Unit testing template with JUnit and Mockito, and integration testing setup.

## Swagger Documentation

Integration with Swagger for API documentation.

## Continuous Integration

Configuration for a CI/CD pipeline (e.g., Jenkins, Travis CI) and a sample pipeline script.

## Monitoring and Metrics

Integration with Spring Boot Actuator and exporting metrics to Prometheus.

## Dockerization

Dockerfile for containerization and Docker Compose for multi-container setups.

## Load Testing

Load testing script using K6:

```shell
cd load-testing
k6 run -e K6_ENV=local -e LOCAL_ACCESS_TOKEN=eyJhbGciOiJIUzI1NiJ9 -e SERVICE_URL=http://localhost:8080/load-test test-template.js
```

## Dashboarding

Example of integrating with Grafana for monitoring dashboards.


