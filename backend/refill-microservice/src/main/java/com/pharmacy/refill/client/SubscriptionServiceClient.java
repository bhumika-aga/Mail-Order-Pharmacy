package com.pharmacy.refill.client;

import com.pharmacy.refill.dto.PrescriptionSummary;
import com.pharmacy.refill.dto.RefillDueResponse;
import com.pharmacy.refill.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * Calls the subscription service with the member's own token, so it only ever
 * sees that member's data. Failures are reported as 503 instead of being turned
 * into an empty (and misleading) result.
 */
@Service
public class SubscriptionServiceClient {
    
    private static final Logger log = LoggerFactory.getLogger(SubscriptionServiceClient.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${subscription.service.url:http://localhost:8082}")
    private String subscriptionServiceUrl;
    
    public SubscriptionServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public List<RefillDueResponse> getRefillDuesAsOfDate(String token, LocalDate dueDate) {
        URI url = UriComponentsBuilder.fromUriString(subscriptionServiceUrl)
                      .path("/api/subscriptions/refillDues")
                      .queryParam("dueDate", dueDate)
                      .encode().build().toUri();
        return getList(url, token, new ParameterizedTypeReference<>() {
        });
    }
    
    /**
     * The prescriptions belonging to the member who owns {@code token}.
     */
    public List<PrescriptionSummary> getMemberPrescriptions(String token) {
        return getList(URI.create(subscriptionServiceUrl + "/api/subscriptions/prescriptions"), token,
            new ParameterizedTypeReference<>() {
            });
    }
    
    private <T> List<T> getList(URI url, String token, ParameterizedTypeReference<List<T>> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        try {
            List<T> body = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), type).getBody();
            return body != null ? body : List.of();
        } catch (RestClientException e) {
            log.warn("Subscription service call failed: {}", e.getMessage());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable",
                "The subscription service could not be reached; please try again", e);
        }
    }
}
