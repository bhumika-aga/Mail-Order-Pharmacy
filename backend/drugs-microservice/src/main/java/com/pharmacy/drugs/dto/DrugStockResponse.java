package com.pharmacy.drugs.dto;

import java.math.BigDecimal;

public class DrugStockResponse {
    private String drugId;
    private String drugName;
    private String location;
    private Integer quantityAvailable;
    private BigDecimal costPerPackage;
    private Boolean isAvailable;
    
    public DrugStockResponse() {
    }
    
    public DrugStockResponse(String drugId, String drugName, String location,
                             Integer quantityAvailable, BigDecimal costPerPackage, Boolean isAvailable) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.location = location;
        this.quantityAvailable = quantityAvailable;
        this.costPerPackage = costPerPackage;
        this.isAvailable = isAvailable;
    }
    
    public String getDrugId() {
        return drugId;
    }
    
    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }
    
    public String getDrugName() {
        return drugName;
    }
    
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }
    
    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }
    
    public BigDecimal getCostPerPackage() {
        return costPerPackage;
    }
    
    public void setCostPerPackage(BigDecimal costPerPackage) {
        this.costPerPackage = costPerPackage;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}