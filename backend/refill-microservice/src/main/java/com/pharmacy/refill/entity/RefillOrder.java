package com.pharmacy.refill.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "refill_orders")
public class RefillOrder {
    
    @Id
    @Column(name = "refill_order_id")
    private String refillOrderId;
    
    @NotBlank(message = "Member ID is required")
    @Column(name = "member_id")
    private String memberId;
    
    @NotNull(message = "Order date is required")
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @NotBlank(message = "Member location is required")
    @Column(name = "member_location")
    private String memberLocation;
    
    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", columnDefinition = "VARCHAR(50)")
    private OrderStatus orderStatus;
    
    @NotNull(message = "Order type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", columnDefinition = "VARCHAR(50)")
    private OrderType orderType;
    
    @Column(name = "subscription_id")
    private String subscriptionId;
    
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
    
    @Column(name = "tracking_number")
    private String trackingNumber;
    
    @OneToMany(mappedBy = "refillOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefillOrderLineItem> lineItems = new ArrayList<>();
    
    public RefillOrder() {
    }
    
    public RefillOrder(String refillOrderId, String memberId, LocalDateTime orderDate,
                       String memberLocation, OrderStatus orderStatus, OrderType orderType,
                       String subscriptionId) {
        this.refillOrderId = refillOrderId;
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.memberLocation = memberLocation;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.subscriptionId = subscriptionId;
    }
    
    // Getters and Setters
    public String getRefillOrderId() {
        return refillOrderId;
    }
    
    public void setRefillOrderId(String refillOrderId) {
        this.refillOrderId = refillOrderId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getMemberLocation() {
        return memberLocation;
    }
    
    public void setMemberLocation(String memberLocation) {
        this.memberLocation = memberLocation;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public OrderType getOrderType() {
        return orderType;
    }
    
    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
    
    public String getSubscriptionId() {
        return subscriptionId;
    }
    
    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public List<RefillOrderLineItem> getLineItems() {
        return lineItems;
    }
    
    public void setLineItems(List<RefillOrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }
    
    public void addLineItem(RefillOrderLineItem lineItem) {
        lineItems.add(lineItem);
        lineItem.setRefillOrder(this);
    }
    
    /**
     * Order lifecycle. Orders only move forward:
     * PENDING -> CONFIRMED -> IN_PROGRESS -> SHIPPED -> DELIVERED, where
     * IN_PROGRESS may be skipped, and an order can be CANCELLED until it ships.
     * DELIVERED and CANCELLED are final.
     */
    public enum OrderStatus {
        PENDING, CONFIRMED, IN_PROGRESS, SHIPPED, DELIVERED, CANCELLED;
        
        public boolean canMoveTo(OrderStatus next) {
            return switch (this) {
                case PENDING -> next == CONFIRMED || next == CANCELLED;
                case CONFIRMED -> next == IN_PROGRESS || next == SHIPPED || next == CANCELLED;
                case IN_PROGRESS -> next == SHIPPED || next == CANCELLED;
                case SHIPPED -> next == DELIVERED;
                case DELIVERED, CANCELLED -> false;
            };
        }
        
        /**
         * Whether the order's stock is reserved but not yet shipped, so that
         * cancelling it must return the stock. (Orders are saved only once their
         * stock is reserved, as CONFIRMED; PENDING is never persisted with stock.)
         */
        public boolean holdsStock() {
            return this == CONFIRMED || this == IN_PROGRESS;
        }
    }
    
    public enum OrderType {
        SCHEDULED, ADHOC
    }
}