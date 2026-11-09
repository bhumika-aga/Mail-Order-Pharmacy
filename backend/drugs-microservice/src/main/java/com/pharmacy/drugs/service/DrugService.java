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
    
    /**
     * Case-insensitive name search over unexpired drugs; the input is matched literally.
     */
    public List<Drug> searchDrugsByName(String drugName) {
        String literal = drugName.trim()
                             .replace("\\", "\\\\")
                             .replace("%", "\\%")
                             .replace("_", "\\_");
        return drugRepository.searchValidDrugsByName("%" + literal + "%", LocalDate.now());
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
            boolean isAvailable = drugLocation.getQuantityAvailable() > 0 && drug.isUnexpiredOn(LocalDate.now());
            
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
        if (quantity <= 0 || isExpiredOrUnknown(drugId)) {
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
        if (quantity <= 0 || isExpiredOrUnknown(drugId)) {
            return false;
        }
        // Check-and-decrement happens in one UPDATE statement, so it is atomic.
        return drugLocationRepository.decrementStock(drugId, location, quantity) == 1;
    }
    
    /**
     * Returns previously reserved stock, e.g. for a cancelled order. A release
     * with a {@code releaseId} is applied at most once: repeating it (a retry
     * after a lost reply) changes nothing and returns true. Returns false if there
     * is no stock row for the drug/location.
     */
    @Transactional
    public boolean releaseStock(String releaseId, String drugId, String location, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        if (releaseId != null && drugLocationRepository.countAppliedReleases(releaseId) > 0) {
            return true; // a retry of a release that was already applied
        }
        if (drugLocationRepository.incrementStock(drugId, location, quantity) != 1) {
            return false;
        }
        if (releaseId != null) {
            // Two concurrent deliveries of one release can both get here; the second
            // insert fails on the primary key and its increment is rolled back.
            drugLocationRepository.recordAppliedRelease(releaseId, drugId, location, quantity);
        }
        return true;
    }
    
    /**
     * True when the drug does not exist or has expired. A drug expiring today
     * counts as expired, matching the {@code expiryDate > today} catalogue queries.
     * (Named so callers need no negation: an inverted check here once let expired
     * stock through while blocking every valid drug.)
     */
    boolean isExpiredOrUnknown(String drugId) {
        LocalDate today = LocalDate.now();
        return drugRepository.findById(drugId)
                   .map(drug -> drug.isExpiredOn(today))
                   .orElse(true);
    }
}