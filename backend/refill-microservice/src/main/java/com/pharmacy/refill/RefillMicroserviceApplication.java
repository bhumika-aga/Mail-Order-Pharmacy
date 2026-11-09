package com.pharmacy.refill;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Duration;

@SpringBootApplication
@EnableScheduling // retries pending stock releases (StockReleaseService)
public class RefillMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RefillMicroserviceApplication.class, args);
    }
    
    /**
     * HTTP client for calls to the drugs and subscription services. Without
     * timeouts a slow or hung service would block request threads indefinitely.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
    
    @Bean
    public RestTemplate restTemplate(@Value("${services.http.connect-timeout:5s}") Duration connectTimeout,
                                     @Value("${services.http.read-timeout:15s}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return new RestTemplate(requestFactory);
    }
}
