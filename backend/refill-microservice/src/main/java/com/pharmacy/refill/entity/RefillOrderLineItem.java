package com.pharmacy.refill.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "refill_order_line_items")
public class RefillOrderLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_item_id")
    private Long lineItemId;
    
    @NotBlank(message = "Drug code is required")
    @Column(name = "drug_code")
    private String drugCode;
    
    @NotBlank(message = "Drug name is required")
    @Column(name = "drug_name")
    private String drugName;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "prescription_id")
    private String prescriptionId;
    
    @NotNull(message = "Unit price is required")
    @Column(name = "unit_price")
    private Double unitPrice;
    
    @Column(name = "total_price")
    private Double totalPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refill_order_id")
    private RefillOrder refillOrder;
    
    public RefillOrderLineItem() {}
    
    public RefillOrderLineItem(String drugCode, String drugName, Integer quantity, 
                              String prescriptionId, Double unitPrice) {
        this.drugCode = drugCode;
        this.drugName = drugName;
        this.quantity = quantity;
        this.prescriptionId = prescriptionId;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
    }
    
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = quantity * unitPrice;
        }
    }
    
    // Getters and Setters
    public Long getLineItemId() { return lineItemId; }
    public void setLineItemId(Long lineItemId) { this.lineItemId = lineItemId; }
    
    public String getDrugCode() { return drugCode; }
    public void setDrugCode(String drugCode) { this.drugCode = drugCode; }
    
    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { 
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    
    public RefillOrder getRefillOrder() { return refillOrder; }
    public void setRefillOrder(RefillOrder refillOrder) { this.refillOrder = refillOrder; }
}