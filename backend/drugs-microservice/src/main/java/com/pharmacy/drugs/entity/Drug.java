package com.pharmacy.drugs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name = "drugs")
public class Drug {
    @Id
    @Column(name = "drug_id")
    private String drugId;
    
    @NotBlank(message = "Drug name is required")
    @Column(name = "drug_name")
    private String drugName;
    
    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;
    
    @NotNull(message = "Manufactured date is required")
    @Column(name = "manufactured_date")
    private LocalDate manufacturedDate;
    
    @NotNull(message = "Expiry date is required")
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @NotNull(message = "Units per package is required")
    @Positive(message = "Units per package must be positive")
    @Column(name = "units_per_package")
    private Integer unitsPerPackage;
    
    @NotNull(message = "Cost per package is required")
    @Positive(message = "Cost per package must be positive")
    @Column(name = "cost_per_package")
    private Double costPerPackage;
    
    @NotBlank(message = "Medical composition is required")
    @Column(name = "medical_composition", columnDefinition = "TEXT")
    private String medicalComposition;
    
    public Drug() {
    }
    
    public Drug(String drugId, String drugName, String manufacturer, LocalDate manufacturedDate,
                LocalDate expiryDate, Integer unitsPerPackage, Double costPerPackage, String medicalComposition) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.manufacturer = manufacturer;
        this.manufacturedDate = manufacturedDate;
        this.expiryDate = expiryDate;
        this.unitsPerPackage = unitsPerPackage;
        this.costPerPackage = costPerPackage;
        this.medicalComposition = medicalComposition;
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
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public LocalDate getManufacturedDate() {
        return manufacturedDate;
    }
    
    public void setManufacturedDate(LocalDate manufacturedDate) {
        this.manufacturedDate = manufacturedDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Integer getUnitsPerPackage() {
        return unitsPerPackage;
    }
    
    public void setUnitsPerPackage(Integer unitsPerPackage) {
        this.unitsPerPackage = unitsPerPackage;
    }
    
    public Double getCostPerPackage() {
        return costPerPackage;
    }
    
    public void setCostPerPackage(Double costPerPackage) {
        this.costPerPackage = costPerPackage;
    }
    
    public String getMedicalComposition() {
        return medicalComposition;
    }
    
    public void setMedicalComposition(String medicalComposition) {
        this.medicalComposition = medicalComposition;
    }
}