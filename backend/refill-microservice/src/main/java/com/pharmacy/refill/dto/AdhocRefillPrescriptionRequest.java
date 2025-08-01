package com.pharmacy.refill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class AdhocRefillPrescriptionRequest {
    @NotBlank(message = "Prescription ID is required")
    private String prescriptionId;
    
    @NotBlank(message = "Drug code is required")
    private String drugCode;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    public AdhocRefillPrescriptionRequest() {}
    
    public AdhocRefillPrescriptionRequest(String prescriptionId, String drugCode, Integer quantity) {
        this.prescriptionId = prescriptionId;
        this.drugCode = drugCode;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
    
    public String getDrugCode() { return drugCode; }
    public void setDrugCode(String drugCode) { this.drugCode = drugCode; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}