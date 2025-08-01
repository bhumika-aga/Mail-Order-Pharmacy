package com.pharmacy.drugs.controller;

import com.pharmacy.drugs.dto.DrugStockRequest;
import com.pharmacy.drugs.dto.DrugStockResponse;
import com.pharmacy.drugs.entity.Drug;
import com.pharmacy.drugs.entity.DrugLocation;
import com.pharmacy.drugs.service.DrugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drugs")
@Tag(name = "Drugs", description = "Drug inventory management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DrugController {
    
    @Autowired
    private DrugService drugService;
    
    @GetMapping("/searchDrugsByID")
    @Operation(summary = "Search drug by ID", description = "Search for a specific drug by its ID")
    public ResponseEntity<?> searchDrugsByID(
            @Parameter(description = "Drug ID to search for", required = true)
            @RequestParam String drugId,
            HttpServletRequest request) {
        
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        
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
            @Parameter(description = "Drug name to search for", required = true)
            @RequestParam String drugName,
            HttpServletRequest request) {
        
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        
        List<Drug> drugs = drugService.searchDrugsByName(drugName);
        return ResponseEntity.ok(drugs);
    }
    
    @PostMapping("/getDispatchableDrugStock")
    @Operation(summary = "Get dispatchable drug stock", 
              description = "Get available stock for specified drugs at a specific location")
    public ResponseEntity<List<DrugStockResponse>> getDispatchableDrugStock(
            @Valid @RequestBody DrugStockRequest request,
            HttpServletRequest httpRequest) {
        
        String memberId = (String) httpRequest.getAttribute("memberId");
        String username = (String) httpRequest.getAttribute("username");
        
        List<DrugStockResponse> stockResponses = drugService.getDispatchableDrugStock(request);
        return ResponseEntity.ok(stockResponses);
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all valid drugs", description = "Get all drugs that are not expired")
    public ResponseEntity<List<Drug>> getAllValidDrugs(HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        
        List<Drug> drugs = drugService.getAllValidDrugs();
        return ResponseEntity.ok(drugs);
    }
    
    @GetMapping("/{drugId}/locations")
    @Operation(summary = "Get drug locations", description = "Get all locations where a specific drug is available")
    public ResponseEntity<List<DrugLocation>> getDrugLocations(
            @Parameter(description = "Drug ID", required = true)
            @PathVariable String drugId,
            HttpServletRequest request) {
        
        String memberId = (String) request.getAttribute("memberId");
        String username = (String) request.getAttribute("username");
        
        List<DrugLocation> locations = drugService.getDrugLocationsByDrugId(drugId);
        return ResponseEntity.ok(locations);
    }
}