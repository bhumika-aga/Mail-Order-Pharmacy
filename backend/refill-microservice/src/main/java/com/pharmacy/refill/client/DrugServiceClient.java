package com.pharmacy.refill.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DrugServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${drugs.service.url:http://localhost:8081}")
    private String drugsServiceUrl;
    
    public DrugServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public Map<String, Object> getDrugDetails(String token, String drugCode) {
        try {
            String url = drugsServiceUrl + "/api/drugs/" + drugCode;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error calling drugs service for drug " + drugCode + ": " + e.getMessage());
            return null;
        }
    }
    
    public boolean checkDrugAvailability(String token, String drugCode, String location, Integer quantity) {
        try {
            String url = drugsServiceUrl + "/api/drugs/availability?drugCode=" + drugCode + 
                        "&location=" + location + "&quantity=" + quantity;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Boolean> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Boolean.class
            );
            
            return response.getBody() != null ? response.getBody() : false;
        } catch (Exception e) {
            System.err.println("Error checking drug availability: " + e.getMessage());
            return false;
        }
    }
    
    public boolean reserveDrugStock(String token, String drugCode, String location, Integer quantity) {
        try {
            String url = drugsServiceUrl + "/api/drugs/reserve";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            Map<String, Object> requestBody = Map.of(
                "drugCode", drugCode,
                "location", location,
                "quantity", quantity
            );
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Boolean> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Boolean.class
            );
            
            return response.getBody() != null ? response.getBody() : false;
        } catch (Exception e) {
            System.err.println("Error reserving drug stock: " + e.getMessage());
            return false;
        }
    }
}