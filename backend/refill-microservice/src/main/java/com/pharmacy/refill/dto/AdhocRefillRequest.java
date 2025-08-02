package com.pharmacy.refill.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AdhocRefillRequest {
    @NotBlank(message = "Member ID is required")
    private String memberId;
    
    @NotBlank(message = "Member location is required")
    private String memberLocation;
    
    @NotEmpty(message = "At least one prescription is required")
    @Valid
    private List<AdhocRefillPrescriptionRequest> prescriptions;
    
    public AdhocRefillRequest() {
    }
    
    public AdhocRefillRequest(String memberId, String memberLocation,
                              List<AdhocRefillPrescriptionRequest> prescriptions) {
        this.memberId = memberId;
        this.memberLocation = memberLocation;
        this.prescriptions = prescriptions;
    }
    
    // Getters and Setters
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
    
    public List<AdhocRefillPrescriptionRequest> getPrescriptions() {
        return prescriptions;
    }
    
    public void setPrescriptions(List<AdhocRefillPrescriptionRequest> prescriptions) {
        this.prescriptions = prescriptions;
    }
}