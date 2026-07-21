package com.pharmacy.refill.client;

import com.pharmacy.refill.dto.RefillDueResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class SubscriptionServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${subscription.service.url:http://localhost:8082}")
    private String subscriptionServiceUrl;
    
    public SubscriptionServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public List<RefillDueResponse> getRefillDuesAsOfDate(String token, LocalDate dueDate) {
        try {
            String url = subscriptionServiceUrl + "/api/subscriptions/refillDues?dueDate=" + dueDate;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List<RefillDueResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });
            
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error calling subscription service: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}