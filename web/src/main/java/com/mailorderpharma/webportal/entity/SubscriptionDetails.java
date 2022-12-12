package com.mailorderpharma.webportal.entity;

import java.time.LocalDate;

import javax.persistence.Id;

//@Data
public class SubscriptionDetails {
	@Id
	private Long subscriptionId;
	private Long prescriptionId;
	private int refillOccurrence;
	private int quantity;
	private String memberId;
	private LocalDate date;
	private String memberLocation;
	private String status;
	private String drugName;

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Long getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(Long prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public int getRefillOccurrence() {
		return refillOccurrence;
	}

	public void setRefillOccurrence(int refillOccurrence) {
		this.refillOccurrence = refillOccurrence;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getMemberLocation() {
		return memberLocation;
	}

	public void setMemberLocation(String memberLocation) {
		this.memberLocation = memberLocation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public SubscriptionDetails(Long subscriptionId, Long prescriptionId, int refillOccurrence, int quantity,
			String memberId, LocalDate date, String memberLocation, String status, String drugName) {
		super();
		this.subscriptionId = subscriptionId;
		this.prescriptionId = prescriptionId;
		this.refillOccurrence = refillOccurrence;
		this.quantity = quantity;
		this.memberId = memberId;
		this.date = date;
		this.memberLocation = memberLocation;
		this.status = status;
		this.drugName = drugName;
	}

	public SubscriptionDetails() {
		super();
	}
}