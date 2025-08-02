package com.pharmacy.swagger.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.swagger.service.SwaggerAggregatorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Swagger Aggregator", description = "Centralized API documentation")
public class SwaggerController {

    @Autowired
    private SwaggerAggregatorService swaggerService;

    @GetMapping("/v3/api-docs/aggregated")
    @Operation(summary = "Get aggregated API documentation", description = "Get combined API documentation from all microservices")
    public Mono<ResponseEntity<Map<String, Object>>> getAggregatedApiDocs() {
        return swaggerService.getAggregatedApiDocs()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/api/services")
    @Operation(summary = "Get service URLs", description = "Get all registered microservice URLs")
    public ResponseEntity<Map<String, String>> getServiceUrls() {
        return ResponseEntity.ok(swaggerService.getServiceUrls());
    }
}

@Controller
class SwaggerUIController {

    @Autowired
    private SwaggerAggregatorService swaggerService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("services", swaggerService.getServiceUrls());
        return "index";
    }
}