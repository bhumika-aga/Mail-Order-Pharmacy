package com.pharmacy.subscription.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "member_prescriptions")
public class MemberPrescription {
    @Id
    @Column(name = "prescription_id")
    private String prescriptionId;

    @NotBlank(message = "Member ID is required")
    @Column(name = "member_id")
    private String memberId;

    @NotBlank(message = "Insurance policy number is required")
    @Column(name = "insurance_policy_number")
    private String insurancePolicyNumber;

    @NotBlank(message = "Insurance provider is required")
    @Column(name = "insurance_provider")
    private String insuranceProvider;

    @NotNull(message = "Prescription date is required")
    @Column(name = "prescription_date")
    private LocalDate prescriptionDate;

    @NotBlank(message = "Drug ID is required")
    @Column(name = "drug_id")
    private String drugId;

    @NotBlank(message = "Dosage per day is required")
    @Column(name = "dosage_per_day")
    private String dosagePerDay;

    @NotNull(message = "Prescription course is required")
    @Positive(message = "Prescription course must be positive")
    @Column(name = "prescription_course")
    private Integer prescriptionCourse;

    @NotBlank(message = "Doctor details are required")
    @Column(name = "doctor_details", columnDefinition = "TEXT")
    private String doctorDetails;

    public MemberPrescription() {
    }

    public MemberPrescription(String prescriptionId, String memberId, String insurancePolicyNumber,
            String insuranceProvider, LocalDate prescriptionDate, String drugId,
            String dosagePerDay, Integer prescriptionCourse, String doctorDetails) {
        this.prescriptionId = prescriptionId;
        this.memberId = memberId;
        this.insurancePolicyNumber = insurancePolicyNumber;
        this.insuranceProvider = insuranceProvider;
        this.prescriptionDate = prescriptionDate;
        this.drugId = drugId;
        this.dosagePerDay = dosagePerDay;
        this.prescriptionCourse = prescriptionCourse;
        this.doctorDetails = doctorDetails;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
}