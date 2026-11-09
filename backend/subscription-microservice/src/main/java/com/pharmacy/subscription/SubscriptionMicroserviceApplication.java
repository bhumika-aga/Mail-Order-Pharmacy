package com.pharmacy.subscription;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Duration;

@SpringBootApplication
public class SubscriptionMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubscriptionMicroserviceApplication.class, args);
    }
    
    /**
     * HTTP client for calls to other services. Without timeouts a slow or hung
     * service would block request threads indefinitely.
     */
    @Bean
    public RestTemplate restTemplate(@Value("${services.http.connect-timeout:5s}") Duration connectTimeout,
                                     @Value("${services.http.read-timeout:15s}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return new RestTemplate(requestFactory);
    }
    
    /**
     * Source of "today" for refill scheduling; replaceable in tests.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
