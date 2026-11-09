package com.pharmacy.subscription;

import com.pharmacy.subscription.client.WakeUpRetryInterceptor;
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
    
    /**
     * Source of "today" for refill scheduling; replaceable in tests.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
