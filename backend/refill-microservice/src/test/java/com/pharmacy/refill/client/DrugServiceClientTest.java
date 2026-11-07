package com.pharmacy.refill.client;

import com.pharmacy.refill.exception.ApiException;
import com.pharmacy.refill.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** Exercises the real HTTP client against a mock server (no network). */
class DrugServiceClientTest {

    private MockRestServiceServer server;
    private DrugServiceClient client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        JwtUtils jwtUtils = new JwtUtils("test-secret-0123456789-0123456789-0123456789-0123456789-0123456789");
        client = new DrugServiceClient(restTemplate, jwtUtils);
        ReflectionTestUtils.setField(client, "drugsServiceUrl", "http://drugs");
    }

    @Test
    void encodesQueryParametersExactlyOnce() {
        // Regression: "New York" was sent as New%2520York (double-encoded), so every
        // availability check failed and no adhoc refill could be placed.
        server.expect(requestTo(URI.create(
                "http://drugs/api/drugs/availability?drugCode=D001&location=New%20York&quantity=3")))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("Authorization", "Bearer member-token"))
            .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        assertThat(client.checkDrugAvailability("member-token", "D001", "New York", 3)).isTrue();
        server.verify();
    }

    @Test
    void reservesStockWithAServiceTokenNotTheMembersToken() {
        server.expect(requestTo("http://drugs/api/drugs/reserve"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("Authorization", org.hamcrest.Matchers.startsWith("Bearer ")))
            .andExpect(header("Authorization", org.hamcrest.Matchers.not("Bearer member-token")))
            .andExpect(jsonPath("$.drugCode").value("D001"))
            .andExpect(jsonPath("$.quantity").value(2))
            .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        assertThat(client.reserveDrugStock("D001", "New York", 2)).isTrue();
        server.verify();
    }

    @Test
    void reportsAnOutageAs503InsteadOfNotAvailable() {
        server.expect(requestTo(org.hamcrest.Matchers.startsWith("http://drugs/api/drugs/availability")))
            .andRespond(withServerError());

        assertThatThrownBy(() -> client.checkDrugAvailability("member-token", "D001", "New York", 1))
            .isInstanceOfSatisfying(ApiException.class,
                e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE));
    }
}
