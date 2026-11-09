package com.pharmacy.refill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stock to return to the drugs service. Its ID travels with the release, so the
 * drugs service can recognise (and ignore) a release it has already applied.
 */
@Entity
@Table(name = "pending_stock_releases")
public class PendingStockRelease {
    
    private static final int MAX_ERROR_LENGTH = 500;
    
    @Id
    @Column(name = "release_id")
    private String releaseId;
    
    @Column(name = "refill_order_id")
    private String refillOrderId;
    
    @Column(name = "drug_code")
    private String drugCode;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "attempts")
    private Integer attempts;
    
    @Column(name = "last_error")
    private String lastError;
    
    protected PendingStockRelease() {
    }
    
    public PendingStockRelease(String refillOrderId, String drugCode, String location, int quantity) {
        this.releaseId = UUID.randomUUID().toString();
        this.refillOrderId = refillOrderId;
        this.drugCode = drugCode;
        this.location = location;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.attempts = 0;
    }
    
    public void recordFailure(String error) {
        attempts++;
        lastError = error == null || error.length() <= MAX_ERROR_LENGTH ? error : error.substring(0, MAX_ERROR_LENGTH);
    }
    
    public String getReleaseId() {
        return releaseId;
    }
    
    public String getRefillOrderId() {
        return refillOrderId;
    }
    
    public String getDrugCode() {
        return drugCode;
    }
    
    public String getLocation() {
        return location;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public String getLastError() {
        return lastError;
    }
}
