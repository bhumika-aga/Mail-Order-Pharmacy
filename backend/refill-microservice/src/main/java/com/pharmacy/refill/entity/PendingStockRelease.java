package com.pharmacy.refill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;
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
    private static final Duration FIRST_RETRY_DELAY = Duration.ofMinutes(1);
    private static final Duration MAX_RETRY_DELAY = Duration.ofHours(1);
    
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
    
    @Column(name = "next_attempt_at")
    private LocalDateTime nextAttemptAt;
    
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
        this.nextAttemptAt = createdAt;
    }
    
    /**
     * Records a failed attempt and schedules the next one: 1 minute after the
     * first failure, doubling each time, at most 1 hour apart.
     */
    public void recordFailure(String error, LocalDateTime now) {
        attempts++;
        lastError = error == null || error.length() <= MAX_ERROR_LENGTH ? error : error.substring(0, MAX_ERROR_LENGTH);
        nextAttemptAt = now.plus(retryDelay());
    }
    
    public Duration retryDelay() {
        int doublings = Math.min(Math.max(attempts - 1, 0), 6); // 2^6 minutes > 1 hour
        Duration delay = FIRST_RETRY_DELAY.multipliedBy(1L << doublings);
        return delay.compareTo(MAX_RETRY_DELAY) > 0 ? MAX_RETRY_DELAY : delay;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getNextAttemptAt() {
        return nextAttemptAt;
    }
}
