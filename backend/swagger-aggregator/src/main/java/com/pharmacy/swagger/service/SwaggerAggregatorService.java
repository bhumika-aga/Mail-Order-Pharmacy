package com.pharmacy.swagger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SwaggerAggregatorService {
    
    @Autowired
    private WebClient webClient;
    
    @Value("${services.auth.url:http://localhost:8084}")
    private String authServiceUrl;
    
    @Value("${services.drugs.url:http://localhost:8081}")
    private String drugsServiceUrl;
    
    @Value("${services.subscription.url:http://localhost:8082}")
    private String subscriptionServiceUrl;
    
    @Value("${services.refill.url:http://localhost:8083}")
    private String refillServiceUrl;
    
    public Mono<Map<String, Object>> getAggregatedApiDocs() {
        Map<String, Object> aggregatedDocs = new HashMap<>();
        
        return Mono.zip(
            getServiceApiDocs("auth", authServiceUrl),
            getServiceApiDocs("drugs", drugsServiceUrl),
            getServiceApiDocs("subscription", subscriptionServiceUrl),
            getServiceApiDocs("refill", refillServiceUrl)).map(tuple -> {
            aggregatedDocs.put("auth", tuple.getT1());
            aggregatedDocs.put("drugs", tuple.getT2());
            aggregatedDocs.put("subscription", tuple.getT3());
            aggregatedDocs.put("refill", tuple.getT4());
            return aggregatedDocs;
        }).onErrorReturn(aggregatedDocs);
    }
    
    private Mono<Object> getServiceApiDocs(String serviceName, String serviceUrl) {
        return webClient.get()
                   .uri(serviceUrl + "/v3/api-docs")
                   .retrieve()
                   .bodyToMono(Object.class)
                   .onErrorReturn(Map.of("error", "Service " + serviceName + " not available"));
    }
    
    public Map<String, String> getServiceUrls() {
        Map<String, String> services = new HashMap<>();
        services.put("auth", authServiceUrl);
        services.put("drugs", drugsServiceUrl);
        services.put("subscription", subscriptionServiceUrl);
        services.put("refill", refillServiceUrl);
        return services;
    }
}