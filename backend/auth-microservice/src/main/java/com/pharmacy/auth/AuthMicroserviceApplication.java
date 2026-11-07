package com.pharmacy.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class AuthMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthMicroserviceApplication.class, args);
    }

    /** Time source for rate limiting and lockouts; replaceable in tests. */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}