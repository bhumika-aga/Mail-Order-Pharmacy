package com.pharmacy.refill.dto;

public class RefillLineItemResponse {
    private Long lineItemId;
    private String drugCode;
    private String drugName;
    private Integer quantity;
    private String prescriptionId;
    private Double unitPrice;
    private Double totalPrice;

    public RefillLineItemResponse() {
    }

    public RefillLineItemResponse(Long lineItemId, String drugCode, String drugName,
            Integer quantity, String prescriptionId,
            Double unitPrice, Double totalPrice) {
        this.lineItemId = lineItemId;
        this.drugCode = drugCode;
        this.drugName = drugName;
        this.quantity = quantity;
        this.prescriptionId = prescriptionId;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public Long getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(Long lineItemId) {
        this.lineItemId = lineItemId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}