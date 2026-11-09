package com.pharmacy.auth.client;

import com.pharmacy.auth.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Erases a member's pharmacy data held by the other services when their account
 * is deleted: refill orders (the refill service returns the stock of orders that
 * had not shipped) and subscriptions with their prescriptions. The calls use a
 * short-lived SERVICE token; members can't call these endpoints themselves.
 * <p>
 * Both endpoints are idempotent, so a deletion that failed half-way is completed
 * by simply trying again.
 */
@Component
public class MemberDataClient {
    
    private static final Logger log = LoggerFactory.getLogger(MemberDataClient.class);
    
    private final RestTemplate restTemplate;
    private final JwtUtils jwtUtils;
    
    // Env vars SUBSCRIPTION_SERVICE_URL / REFILL_SERVICE_URL bind to these
    @Value("${subscription.service.url:http://localhost:8082}")
    private String subscriptionServiceUrl;
    
    @Value("${refill.service.url:http://localhost:8083}")
    private String refillServiceUrl;
    
    public MemberDataClient(RestTemplate restTemplate, JwtUtils jwtUtils) {
        this.restTemplate = restTemplate;
        this.jwtUtils = jwtUtils;
    }
    
    /**
     * @throws MemberDataErasureException if either service could not erase the data
     */
    public void eraseMemberData(String memberId) {
        erase("refill", refillServiceUrl, "/api/refill/members/{memberId}", memberId);
        erase("subscription", subscriptionServiceUrl, "/api/subscriptions/members/{memberId}", memberId);
    }
    
    private void erase(String service, String baseUrl, String path, String memberId) {
        URI url = UriComponentsBuilder.fromUriString(baseUrl).path(path)
                      .buildAndExpand(memberId).encode().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtils.generateServiceToken());
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        } catch (RestClientException e) {
            log.error("Could not erase data of member {} in the {} service: {}", memberId, service, e.getMessage());
            throw new MemberDataErasureException(service, e);
        }
    }
    
    /**
     * A member's data could not be erased in another service (unreachable, or it refused).
     */
    public static class MemberDataErasureException extends RuntimeException {
        
        private final String service;
        
        public MemberDataErasureException(String service, Throwable cause) {
            super("Member data could not be erased in the " + service + " service", cause);
            this.service = service;
        }
        
        public String getService() {
            return service;
        }
    }
}
