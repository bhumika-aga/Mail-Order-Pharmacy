package com.pharmacy.drugs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "drug_locations")
@IdClass(DrugLocationId.class)
public class DrugLocation {
    @Id
    @Column(name = "drug_id")
    private String drugId;
    
    @Id
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Quantity available is required")
    @PositiveOrZero(message = "Quantity available must be zero or positive")
    @Column(name = "quantity_available")
    private Integer quantityAvailable;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_id", insertable = false, updatable = false)
    private Drug drug;
    
    public DrugLocation() {
    }
    
    public DrugLocation(String drugId, String location, Integer quantityAvailable) {
        this.drugId = drugId;
        this.location = location;
        this.quantityAvailable = quantityAvailable;
    }
    
    public String getDrugId() {
        return drugId;
    }
    
    public void setDrugId(String drugId) {
        this.drugId = drugId;
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
    
    public Drug getDrug() {
        return drug;
    }
    
    public void setDrug(Drug drug) {
        this.drug = drug;
    }
}