package com.pharmacy.subscription.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "member_subscriptions")
public class MemberSubscription {
    @Id
    @Column(name = "subscription_id")
    private String subscriptionId;
    
    @NotBlank(message = "Member ID is required")
    @Column(name = "member_id")
    private String memberId;
    
    @NotNull(message = "Subscription date is required")
    @Column(name = "subscription_date")
    private LocalDate subscriptionDate;
    
    @NotBlank(message = "Prescription ID is required")
    @Column(name = "prescription_id")
    private String prescriptionId;
    
    @NotBlank(message = "Refill frequency is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "refill_frequency")
    private RefillFrequency refillFrequency;
    
    @NotBlank(message = "Member location is required")
    @Column(name = "member_location")
    private String memberLocation;
    
    @NotBlank(message = "Subscription status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", insertable = false, updatable = false)
    private MemberPrescription prescription;
    
    public enum RefillFrequency {
        WEEKLY, MONTHLY
    }
    
    public enum SubscriptionStatus {
        ACTIVE, INACTIVE
    }
    
    public MemberSubscription() {}
    
    public MemberSubscription(String subscriptionId, String memberId, LocalDate subscriptionDate,
                             String prescriptionId, RefillFrequency refillFrequency, 
                             String memberLocation, SubscriptionStatus subscriptionStatus) {
        this.subscriptionId = subscriptionId;
        this.memberId = memberId;
        this.subscriptionDate = subscriptionDate;
        this.prescriptionId = prescriptionId;
        this.refillFrequency = refillFrequency;
        this.memberLocation = memberLocation;
        this.subscriptionStatus = subscriptionStatus;
    }
    
    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
    
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    
    public LocalDate getSubscriptionDate() { return subscriptionDate; }
    public void setSubscriptionDate(LocalDate subscriptionDate) { this.subscriptionDate = subscriptionDate; }
    
    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
    
    public RefillFrequency getRefillFrequency() { return refillFrequency; }
    public void setRefillFrequency(RefillFrequency refillFrequency) { this.refillFrequency = refillFrequency; }
    
    public String getMemberLocation() { return memberLocation; }
    public void setMemberLocation(String memberLocation) { this.memberLocation = memberLocation; }
    
    public SubscriptionStatus getSubscriptionStatus() { return subscriptionStatus; }
    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) { this.subscriptionStatus = subscriptionStatus; }
    
    public MemberPrescription getPrescription() { return prescription; }
    public void setPrescription(MemberPrescription prescription) { this.prescription = prescription; }
}