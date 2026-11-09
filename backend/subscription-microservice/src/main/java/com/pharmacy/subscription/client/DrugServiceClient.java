package com.pharmacy.subscription.client;

import com.pharmacy.subscription.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class DrugServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${services.drugs.url:http://localhost:8081}")
    private String drugsServiceUrl;
    
    public DrugServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Whether at least one unit of the drug is in stock and unexpired at the location.
     * A GET, so it can safely be retried while a sleeping drugs service wakes up.
     *
     * @throws ApiException (503) if the drugs service cannot be reached, so callers
     *                      do not mistake an outage for "drug not available"
     */
    public boolean verifyDrugAvailability(String drugId, String location, String token) {
        URI url = UriComponentsBuilder.fromUriString(drugsServiceUrl)
                      .path("/api/drugs/availability")
                      .queryParam("drugCode", drugId)
                      .queryParam("location", location)
                      .queryParam("quantity", 1)
                      .encode().build().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        try {
            Boolean available = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Boolean.class)
                                    .getBody();
            return Boolean.TRUE.equals(available);
        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
                "Drug availability could not be checked right now; please try again", e);
        }
    }
}
