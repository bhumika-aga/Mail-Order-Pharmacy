package com.pharmacy.drugs.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class DrugStockRequest {
    @NotEmpty(message = "Drug IDs list cannot be empty")
    private List<String> drugIds;

    @NotBlank(message = "Location is required")
    private String location;

    public DrugStockRequest() {
    }

    public DrugStockRequest(List<String> drugIds, String location) {
        this.drugIds = drugIds;
        this.location = location;
    }

    public List<String> getDrugIds() {
        return drugIds;
    }

    public void setDrugIds(List<String> drugIds) {
        this.drugIds = drugIds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}