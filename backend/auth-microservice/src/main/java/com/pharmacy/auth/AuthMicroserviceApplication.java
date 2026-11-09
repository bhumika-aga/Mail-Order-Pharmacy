package com.pharmacy.auth;

import com.pharmacy.auth.client.WakeUpRetryInterceptor;
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
                                     @Value("${services.http.read-timeout:15s}") Duration readTimeout,
                                     @Value("${services.http.wake-up-wait:0s}") Duration wakeUpWait) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        // Hosts that put idle services to sleep (e.g. Render's free tier): wait for
        // them to wake up instead of failing (env SERVICES_HTTP_WAKEUPWAIT, e.g. 90s).
        if (!wakeUpWait.isZero()) {
            restTemplate.getInterceptors().add(new WakeUpRetryInterceptor(wakeUpWait));
        }
        return restTemplate;
    }
}