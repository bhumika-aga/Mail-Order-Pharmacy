package com.pharmacy.subscription.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DrugServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.drugs.url:http://localhost:8081}")
    private String drugsServiceUrl;

    public boolean verifyDrugAvailability(String drugId, String location, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            Map<String, Object> request = Map.of(
                    "drugIds", List.of(drugId),
                    "location", location);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ParameterizedTypeReference<List<Map<String, Object>>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    drugsServiceUrl + "/api/drugs/getDispatchableDrugStock",
                    HttpMethod.POST,
                    entity,
                    responseType);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> stockResponses = response.getBody();
                return stockResponses.stream()
                        .anyMatch(stock -> Boolean.TRUE.equals(stock.get("isAvailable")));
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error verifying drug availability: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getDrugDetails(String drugId, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    drugsServiceUrl + "/api/drugs/searchDrugsByID?drugId=" + drugId,
                    HttpMethod.GET,
                    entity,
                    responseType);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error getting drug details: " + e.getMessage());
            return null;
        }
    }
}