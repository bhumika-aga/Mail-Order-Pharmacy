package com.pharmacy.refill.dto;

public class RefillDuePrescription {
    private String prescriptionId;
    private String drugCode;
    private String drugName;
    private Integer dosage;
    private String drugForm;
    private Integer quantity;
    private Integer refillsRemaining;

    public RefillDuePrescription() {
    }

    public RefillDuePrescription(String prescriptionId, String drugCode, String drugName,
            Integer dosage, String drugForm, Integer quantity,
            Integer refillsRemaining) {
        this.prescriptionId = prescriptionId;
        this.drugCode = drugCode;
        this.drugName = drugName;
        this.dosage = dosage;
        this.drugForm = drugForm;
        this.quantity = quantity;
        this.refillsRemaining = refillsRemaining;
    }

    // Getters and Setters
    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getDrugCode() {
        return drugCode;
    }

    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public Integer getDosage() {
        return dosage;
    }

    public void setDosage(Integer dosage) {
        this.dosage = dosage;
    }

    public String getDrugForm() {
        return drugForm;
    }

    public void setDrugForm(String drugForm) {
        this.drugForm = drugForm;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRefillsRemaining() {
        return refillsRemaining;
    }

    public void setRefillsRemaining(Integer refillsRemaining) {
        this.refillsRemaining = refillsRemaining;
    }
}