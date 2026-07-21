package com.pharmacy.swagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Thin Swagger UI host. It exposes no APIs of its own — springdoc's
 * {@code swagger-ui.urls} configuration renders a single Swagger UI with a
 * dropdown that switches between each microservice's live OpenAPI spec.
 */
@SpringBootApplication
public class SwaggerAggregatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwaggerAggregatorApplication.class, args);
    }
}
