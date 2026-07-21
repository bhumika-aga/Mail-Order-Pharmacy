package com.pharmacy.refill.dto;

/**
 * A prescription entry within a refill-due response. Field names mirror the
 * payload produced by the subscription service so the values map through
 * cleanly when this service proxies {@code getRefillDuesAsOfDate}.
 */
public class RefillDuePrescription {
    private String prescriptionId;
    private String drugId;
    private String dosagePerDay;
    private Integer prescriptionCourse;
    
    public RefillDuePrescription() {
    }
    
    public RefillDuePrescription(String prescriptionId, String drugId, String dosagePerDay,
                                 Integer prescriptionCourse) {
        this.prescriptionId = prescriptionId;
        this.drugId = drugId;
        this.dosagePerDay = dosagePerDay;
        this.prescriptionCourse = prescriptionCourse;
    }
    
    public String getPrescriptionId() {
        return prescriptionId;
    }
    
    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
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
}
