package com.pharmacy.refill.dto;

import java.time.LocalDate;
import java.util.List;

public class RefillDueResponse {
    private String subscriptionId;
    private String memberId;
    private String memberLocation;
    private LocalDate dueDate;
    private String refillFrequency;
    private List<RefillDuePrescription> prescriptions;

    public RefillDueResponse() {
    }

    public RefillDueResponse(String subscriptionId, String memberId, String memberLocation,
            LocalDate dueDate, String refillFrequency,
            List<RefillDuePrescription> prescriptions) {
        this.subscriptionId = subscriptionId;
        this.memberId = memberId;
        this.memberLocation = memberLocation;
        this.dueDate = dueDate;
        this.refillFrequency = refillFrequency;
        this.prescriptions = prescriptions;
    }

    // Getters and Setters
    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberLocation() {
        return memberLocation;
    }

    public void setMemberLocation(String memberLocation) {
        this.memberLocation = memberLocation;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getRefillFrequency() {
        return refillFrequency;
    }

    public void setRefillFrequency(String refillFrequency) {
        this.refillFrequency = refillFrequency;
    }

    public List<RefillDuePrescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<RefillDuePrescription> prescriptions) {
        this.prescriptions = prescriptions;
    }
}