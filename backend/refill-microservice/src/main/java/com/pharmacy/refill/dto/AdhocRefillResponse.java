package com.pharmacy.refill.dto;

import java.time.LocalDateTime;

public class AdhocRefillResponse {
    private String refillOrderId;
    private String memberId;
    private LocalDateTime orderDate;
    private String memberLocation;
    private String orderStatus;
    private String message;
    private Double totalAmount;

    public AdhocRefillResponse() {
    }

    public AdhocRefillResponse(String refillOrderId, String memberId, LocalDateTime orderDate,
            String memberLocation, String orderStatus, String message,
            Double totalAmount) {
        this.refillOrderId = refillOrderId;
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.memberLocation = memberLocation;
        this.orderStatus = orderStatus;
        this.message = message;
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}