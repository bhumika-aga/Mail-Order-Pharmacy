package com.pharmacy.drugs.service;

import com.pharmacy.drugs.dto.DrugStockRequest;
import com.pharmacy.drugs.dto.DrugStockResponse;
import com.pharmacy.drugs.entity.Drug;
import com.pharmacy.drugs.entity.DrugLocation;
import com.pharmacy.drugs.entity.DrugLocationId;
import com.pharmacy.drugs.repository.DrugLocationRepository;
import com.pharmacy.drugs.repository.DrugRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DrugService {
    
    private final DrugRepository drugRepository;
    private final DrugLocationRepository drugLocationRepository;
    
    public DrugService(DrugRepository drugRepository, DrugLocationRepository drugLocationRepository) {
        this.drugRepository = drugRepository;
        this.drugLocationRepository = drugLocationRepository;
    }
    
    
    public Optional<Drug> searchDrugById(String drugId) {
        return drugRepository.findById(drugId);
    }
    
    public List<Drug> searchDrugsByName(String drugName) {
        return drugRepository.findByDrugNameContainingIgnoreCaseAndValidExpiry(drugName, LocalDate.now());
    }
    
    public List<DrugStockResponse> getDispatchableDrugStock(DrugStockRequest request) {
        List<DrugLocation> drugLocations = drugLocationRepository
                                               .findByDrugIdsAndLocation(request.getDrugIds(), request.getLocation());
        
        return drugLocations.stream()
                   .map(this::mapToDrugStockResponse)
                   .collect(Collectors.toList());
    }
    
    private DrugStockResponse mapToDrugStockResponse(DrugLocation drugLocation) {
        Optional<Drug> drugOpt = drugRepository.findById(drugLocation.getDrugId());
        
        if (drugOpt.isPresent()) {
            Drug drug = drugOpt.get();
            boolean isExpired = drug.getExpiryDate().isBefore(LocalDate.now());
            boolean isAvailable = drugLocation.getQuantityAvailable() > 0 && !isExpired;
            
            return new DrugStockResponse(
                drug.getDrugId(),
                drug.getDrugName(),
                drugLocation.getLocation(),
                drugLocation.getQuantityAvailable(),
                drug.getCostPerPackage(),
                isAvailable);
        }
        
        return new DrugStockResponse(
            drugLocation.getDrugId(),
            "Unknown Drug",
            drugLocation.getLocation(),
            drugLocation.getQuantityAvailable(),
            BigDecimal.ZERO,
            false);
    }
    
    public List<Drug> getAllValidDrugs() {
        return drugRepository.findValidDrugs(LocalDate.now());
    }
    
    public List<DrugLocation> getDrugLocationsByDrugId(String drugId) {
        return drugLocationRepository.findByDrugId(drugId);
    }
    
    /**
     * Checks whether the requested quantity of a non-expired drug can be
     * dispatched from the given location.
     */
    public boolean isDispatchable(String drugId, String location, int quantity) {
        if (quantity <= 0 || isValidDrug(drugId)) {
            return false;
        }
        return drugLocationRepository.findById(new DrugLocationId(drugId, location))
                   .map(loc -> loc.getQuantityAvailable() >= quantity)
                   .orElse(false);
    }
    
    /**
     * Atomically reserves stock by decrementing the available quantity for a
     * drug at a location. Returns false (without mutating) if the drug is
     * expired/unknown or there is insufficient stock.
     */
    @Transactional
    public boolean reserveStock(String drugId, String location, int quantity) {
        if (quantity <= 0 || isValidDrug(drugId)) {
            return false;
        }
        return drugLocationRepository.findById(new DrugLocationId(drugId, location))
                   .filter(loc -> loc.getQuantityAvailable() >= quantity)
                   .map(loc -> {
                       loc.setQuantityAvailable(loc.getQuantityAvailable() - quantity);
                       drugLocationRepository.save(loc);
                       return true;
                   })
                   .orElse(false);
    }
    
    private boolean isValidDrug(String drugId) {
        return drugRepository.findById(drugId)
                   .map(drug -> drug.getExpiryDate().isAfter(LocalDate.now()))
                   .orElse(false);
    }
}