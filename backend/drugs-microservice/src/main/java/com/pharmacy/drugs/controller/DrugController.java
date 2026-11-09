package com.pharmacy.drugs.controller;

import com.pharmacy.drugs.dto.DrugStockRequest;
import com.pharmacy.drugs.dto.DrugStockResponse;
import com.pharmacy.drugs.dto.StockReservationRequest;
import com.pharmacy.drugs.entity.Drug;
import com.pharmacy.drugs.entity.DrugLocation;
import com.pharmacy.drugs.service.DrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drugs")
@Tag(name = "Drugs", description = "Drug inventory management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DrugController {
    
    private final DrugService drugService;
    
    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }
    
    @GetMapping("/searchDrugsByID")
    @Operation(summary = "Search drug by ID", description = "Search for a specific drug by its ID")
    public ResponseEntity<?> searchDrugsByID(
        @Parameter(description = "Drug ID to search for", required = true) @RequestParam String drugId,
        HttpServletRequest request) {
        
        Optional<Drug> drug = drugService.searchDrugById(drugId);
        
        if (drug.isPresent()) {
            return ResponseEntity.ok(drug.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/searchDrugsByName")
    @Operation(summary = "Search drugs by name", description = "Search for drugs containing the specified name")
    public ResponseEntity<List<Drug>> searchDrugsByName(
        @Parameter(description = "Drug name to search for", required = true) @RequestParam String drugName,
        HttpServletRequest request) {
        
        List<Drug> drugs = drugService.searchDrugsByName(drugName);
        return ResponseEntity.ok(drugs);
    }
    
    @PostMapping("/getDispatchableDrugStock")
    @Operation(summary = "Get dispatchable drug stock", description = "Get available stock for specified drugs at a specific location")
    public ResponseEntity<List<DrugStockResponse>> getDispatchableDrugStock(
        @Valid @RequestBody DrugStockRequest request,
        HttpServletRequest httpRequest) {
        
        List<DrugStockResponse> stockResponses = drugService.getDispatchableDrugStock(request);
        return ResponseEntity.ok(stockResponses);
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all valid drugs", description = "Get all drugs that are not expired")
    public ResponseEntity<List<Drug>> getAllValidDrugs(HttpServletRequest request) {
        
        List<Drug> drugs = drugService.getAllValidDrugs();
        return ResponseEntity.ok(drugs);
    }
    
    @GetMapping("/{drugId}/locations")
    @Operation(summary = "Get drug locations", description = "Get all locations where a specific drug is available")
    public ResponseEntity<List<DrugLocation>> getDrugLocations(
        @Parameter(description = "Drug ID", required = true) @PathVariable String drugId,
        HttpServletRequest request) {
        
        List<DrugLocation> locations = drugService.getDrugLocationsByDrugId(drugId);
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/availability")
    @Operation(summary = "Check drug availability", description = "Check whether the requested quantity of a non-expired drug can be dispatched from a location")
    public ResponseEntity<Boolean> checkAvailability(
        @Parameter(description = "Drug code", required = true) @RequestParam String drugCode,
        @Parameter(description = "Dispatch location", required = true) @RequestParam String location,
        @Parameter(description = "Requested quantity", required = true) @RequestParam Integer quantity) {
        
        return ResponseEntity.ok(drugService.isDispatchable(drugCode, location, quantity));
    }
    
    // Stock changes are made only by other services (the refill service), using a
    // short-lived SERVICE token; members cannot reserve or release stock directly.
    
    @PostMapping("/reserve")
    @PreAuthorize("hasRole('SERVICE')")
    @Operation(summary = "Reserve drug stock",
        description = "Atomically decrement available stock for a drug at a location (internal: service token only)")
    public ResponseEntity<Boolean> reserveStock(@Valid @RequestBody StockReservationRequest request) {
        return ResponseEntity.ok(
            drugService.reserveStock(request.drugCode(), request.location(), request.quantity()));
    }
    
    @PostMapping("/release")
    @PreAuthorize("hasRole('SERVICE')")
    @Operation(summary = "Release reserved drug stock",
        description = "Return previously reserved stock, e.g. when an order fails part-way (internal: service token only)")
    public ResponseEntity<Boolean> releaseStock(@Valid @RequestBody StockReservationRequest request) {
        return ResponseEntity.ok(
            drugService.releaseStock(request.drugCode(), request.location(), request.quantity()));
    }
}