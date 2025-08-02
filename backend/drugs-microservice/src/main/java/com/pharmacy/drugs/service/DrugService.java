package com.pharmacy.drugs.service;

import com.pharmacy.drugs.dto.DrugStockRequest;
import com.pharmacy.drugs.dto.DrugStockResponse;
import com.pharmacy.drugs.entity.Drug;
import com.pharmacy.drugs.entity.DrugLocation;
import com.pharmacy.drugs.repository.DrugLocationRepository;
import com.pharmacy.drugs.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DrugService {
    
    @Autowired
    private DrugRepository drugRepository;
    
    @Autowired
    private DrugLocationRepository drugLocationRepository;
    
    public Optional<Drug> searchDrugById(String drugId) {
        return drugRepository.findById(drugId);
    }
    
    public List<Drug> searchDrugsByName(String drugName) {
        return drugRepository.findByDrugNameContainingIgnoreCaseAndValidExpiry(drugName, LocalDate.now());
    }
    
    public List<DrugStockResponse> getDispatchableDrugStock(DrugStockRequest request) {
        List<DrugLocation> drugLocations = drugLocationRepository
                                               .findByDrugIdsAndLocation(request.getDrugIds(), request.getLocation());
        
        return drugLocations.parallelStream()
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
            0.0,
            false);
    }
    
    public List<Drug> getAllValidDrugs() {
        return drugRepository.findValidDrugs(LocalDate.now());
    }
    
    public List<DrugLocation> getDrugLocationsByDrugId(String drugId) {
        return drugLocationRepository.findByDrugId(drugId);
    }
}