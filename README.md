# tech-scaffoldings

## Overview

Ready to use code templates in Java.

## Table of Contents

1. [Asynchronous Processing](#asynchronous-processing)
2. [Caching](#caching)
3. [Testing](#testing)
4. [Monitoring and Metrics](#monitoring-and-metrics)
5. [Load Testing](#load-testing)
6. [Dashboarding](#dashboarding)

## Asynchronous Processing

[async-api](https://github.com/Pulin412/tech-scaffoldings/blob/main/async-apis/README.md)

Multiple ways to do async API calls and comparisons.

## Caching

Integration with Spring Cache for caching and an example of caching a method result.

## Testing

Unit testing template with JUnit and Mockito, and integration testing setup.

## Monitoring and Metrics

Integration with Spring Boot Actuator and exporting metrics to Prometheus.

## Load Testing

Start the application:

```shell
cd k6-load-testing
mvn spring-boot:run
```

Run the test script : 

```shell
cd k6-load-testing/scripts
k6 run -e K6_ENV=local -e LOCAL_ACCESS_TOKEN=eyJhbGciOiJIUzI1NiJ9 -e SERVICE_URL=http://localhost:8080/load-test test-template.js
```

## Dashboarding

Example of integrating with Grafana for monitoring dashboards.


