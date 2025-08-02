package com.pharmacy.subscription.dto;

import java.time.LocalDate;

import com.pharmacy.subscription.entity.MemberSubscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SubscriptionRequest {
    @NotBlank(message = "Insurance policy number is required")
    private String insurancePolicyNumber;

    @NotBlank(message = "Insurance provider is required")
    private String insuranceProvider;

    @NotNull(message = "Prescription date is required")
    private LocalDate prescriptionDate;

    @NotBlank(message = "Drug ID is required")
    private String drugId;

    @NotBlank(message = "Dosage per day is required")
    private String dosagePerDay;

    @NotNull(message = "Prescription course is required")
    @Positive(message = "Prescription course must be positive")
    private Integer prescriptionCourse;

    @NotBlank(message = "Doctor details are required")
    private String doctorDetails;

    @NotNull(message = "Refill frequency is required")
    private MemberSubscription.RefillFrequency refillFrequency;

    @NotBlank(message = "Member location is required")
    private String memberLocation;

    public SubscriptionRequest() {
    }

    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDosagePerDay() {
        return dosagePerDay;
    }

    public void setDosagePerDay(String dosagePerDay) {
        this.dosagePerDay = dosagePerDay;
    }

    public Integer getPrescriptionCourse() {
        return prescriptionCourse;
    }

    public void setPrescriptionCourse(Integer prescriptionCourse) {
        this.prescriptionCourse = prescriptionCourse;
    }

    public String getDoctorDetails() {
        return doctorDetails;
    }

    public void setDoctorDetails(String doctorDetails) {
        this.doctorDetails = doctorDetails;
    }

    public MemberSubscription.RefillFrequency getRefillFrequency() {
        return refillFrequency;
    }

    public void setRefillFrequency(MemberSubscription.RefillFrequency refillFrequency) {
        this.refillFrequency = refillFrequency;
    }

    public String getMemberLocation() {
        return memberLocation;
    }

    public void setMemberLocation(String memberLocation) {
        this.memberLocation = memberLocation;
    }
}