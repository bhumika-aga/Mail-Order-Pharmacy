package com.pharmacy.subscription.client;

import com.pharmacy.subscription.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class DrugServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${services.drugs.url:http://localhost:8081}")
    private String drugsServiceUrl;
    
    public DrugServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Whether the drug is in stock and unexpired at the location.
     *
     * @throws ApiException (503) if the drugs service cannot be reached, so callers
     *                      do not mistake an outage for "drug not available"
     */
    public boolean verifyDrugAvailability(String drugId, String location, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        Map<String, Object> request = Map.of("drugIds", List.of(drugId), "location", location);
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                drugsServiceUrl + "/api/drugs/getDispatchableDrugStock",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {
                });
            List<Map<String, Object>> stock = response.getBody();
            return stock != null && stock.stream().anyMatch(s -> Boolean.TRUE.equals(s.get("isAvailable")));
        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
                "Drug availability could not be checked right now; please try again", e);
        }
    }
}
