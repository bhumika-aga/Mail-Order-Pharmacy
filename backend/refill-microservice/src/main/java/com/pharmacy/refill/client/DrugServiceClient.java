package com.pharmacy.refill.client;

import com.pharmacy.refill.exception.ApiException;
import com.pharmacy.refill.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * Calls the drugs service. Read calls use the member's own token; stock changes
 * use a short-lived SERVICE token, because members may not reserve or release
 * stock directly. Any failure to reach the service is reported as 503 rather
 * than being mistaken for "not available".
 */
@Service
public class DrugServiceClient {

    private static final Logger log = LoggerFactory.getLogger(DrugServiceClient.class);

    private final RestTemplate restTemplate;
    private final JwtUtils jwtUtils;

    @Value("${drugs.service.url:http://localhost:8081}")
    private String drugsServiceUrl;

    public DrugServiceClient(RestTemplate restTemplate, JwtUtils jwtUtils) {
        this.restTemplate = restTemplate;
        this.jwtUtils = jwtUtils;
    }

    /** Drug details (name, cost...), or empty if the drug code does not exist. */
    public Optional<Map<String, Object>> getDrugDetails(String token, String drugCode) {
        URI url = UriComponentsBuilder.fromUriString(drugsServiceUrl)
                         .path("/api/drugs/searchDrugsByID")
                         .queryParam("drugId", drugCode)
                         .encode().build().toUri();
        try {
            return Optional.ofNullable(restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(bearer(token)),
                new ParameterizedTypeReference<Map<String, Object>>() {
                }).getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw unavailable(e);
        }
    }

    public boolean checkDrugAvailability(String token, String drugCode, String location, int quantity) {
        URI url = UriComponentsBuilder.fromUriString(drugsServiceUrl)
                         .path("/api/drugs/availability")
                         .queryParam("drugCode", drugCode)
                         .queryParam("location", location)
                         .queryParam("quantity", quantity)
                         .encode().build().toUri();
        try {
            Boolean available = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(bearer(token)), Boolean.class).getBody();
            return Boolean.TRUE.equals(available);
        } catch (RestClientException e) {
            throw unavailable(e);
        }
    }

    /** Atomically reserves stock; false if there is not enough (or the drug expired meanwhile). */
    public boolean reserveDrugStock(String drugCode, String location, int quantity) {
        return changeStock("/api/drugs/reserve", drugCode, location, quantity);
    }

    /** Returns stock reserved earlier for an order that could not be completed. */
    public boolean releaseDrugStock(String drugCode, String location, int quantity) {
        return changeStock("/api/drugs/release", drugCode, location, quantity);
    }

    private boolean changeStock(String path, String drugCode, String location, int quantity) {
        HttpHeaders headers = bearer(jwtUtils.generateServiceToken());
        Map<String, Object> body = Map.of("drugCode", drugCode, "location", location, "quantity", quantity);
        try {
            Boolean changed = restTemplate.exchange(
                drugsServiceUrl + path, HttpMethod.POST, new HttpEntity<>(body, headers), Boolean.class).getBody();
            return Boolean.TRUE.equals(changed);
        } catch (RestClientException e) {
            throw unavailable(e);
        }
    }

    private static HttpHeaders bearer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private static ApiException unavailable(RestClientException e) {
        log.warn("Drugs service call failed: {}", e.getMessage());
        return new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable",
            "The drugs service could not be reached; please try again", e);
    }
}
