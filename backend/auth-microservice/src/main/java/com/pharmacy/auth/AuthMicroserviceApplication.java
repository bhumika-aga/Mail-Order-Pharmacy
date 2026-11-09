package com.pharmacy.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Duration;

@SpringBootApplication
public class AuthMicroserviceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthMicroserviceApplication.class, args);
    }
    
    /**
     * Time source for rate limiting and lockouts; replaceable in tests.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
    
    /**
     * HTTP client for erasing a deleted member's data in the other services.
     * Without timeouts a hung service would block the request thread indefinitely.
     */
    @Bean
    public RestTemplate restTemplate(@Value("${services.http.connect-timeout:5s}") Duration connectTimeout,
                                     @Value("${services.http.read-timeout:15s}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return new RestTemplate(requestFactory);
    }
}