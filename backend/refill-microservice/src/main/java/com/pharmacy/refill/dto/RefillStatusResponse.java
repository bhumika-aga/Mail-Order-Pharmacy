package com.pharmacy.refill.dto;

import com.pharmacy.refill.entity.RefillOrder;

import java.time.LocalDateTime;
import java.util.List;

public class RefillStatusResponse {
    private String refillOrderId;
    private String memberId;
    private LocalDateTime orderDate;
    private String memberLocation;
    private RefillOrder.OrderStatus orderStatus;
    private RefillOrder.OrderType orderType;
    private String subscriptionId;
    private LocalDateTime deliveryDate;
    private String trackingNumber;
    private List<RefillLineItemResponse> lineItems;
    private Double totalAmount;
    
    public RefillStatusResponse() {
    }
    
    public RefillStatusResponse(String refillOrderId, String memberId, LocalDateTime orderDate,
                                String memberLocation, RefillOrder.OrderStatus orderStatus,
                                RefillOrder.OrderType orderType, String subscriptionId,
                                LocalDateTime deliveryDate, String trackingNumber,
                                List<RefillLineItemResponse> lineItems, Double totalAmount) {
        this.refillOrderId = refillOrderId;
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.memberLocation = memberLocation;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.subscriptionId = subscriptionId;
        this.deliveryDate = deliveryDate;
        this.trackingNumber = trackingNumber;
        this.lineItems = lineItems;
        this.totalAmount = totalAmount;
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
    
    public RefillOrder.OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(RefillOrder.OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public RefillOrder.OrderType getOrderType() {
        return orderType;
    }
    
    public void setOrderType(RefillOrder.OrderType orderType) {
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
    
    public List<RefillLineItemResponse> getLineItems() {
        return lineItems;
    }
    
    public void setLineItems(List<RefillLineItemResponse> lineItems) {
        this.lineItems = lineItems;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}