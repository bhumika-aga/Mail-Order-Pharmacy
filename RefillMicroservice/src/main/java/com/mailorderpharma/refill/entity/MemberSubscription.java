package com.mailorderpharma.refill.entity;

import java.time.LocalDate;

//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
public class MemberSubscription {

	private long subscriptionId;
	private long prescriptionId;
	private String memberId;
	private LocalDate date;
	private int quantity;
	private String drugName;
	private int refillOccurrence; // it is used to tell occurrence i.e, the cycle of refill
	private String memberLocation;
	private String status;

	public long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public long getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(long prescriptionId) {
		this.prescriptionId = prescriptionId;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public int getRefillOccurrence() {
		return refillOccurrence;
	}

	public void setRefillOccurrence(int refillOccurrence) {
		this.refillOccurrence = refillOccurrence;
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

	public MemberSubscription(long subscriptionId, long prescriptionId, String memberId, LocalDate date, int quantity,
			String drugName, int refillOccurrence, String memberLocation, String status) {
		super();
		this.subscriptionId = subscriptionId;
		this.prescriptionId = prescriptionId;
		this.memberId = memberId;
		this.date = date;
		this.quantity = quantity;
		this.drugName = drugName;
		this.refillOccurrence = refillOccurrence;
		this.memberLocation = memberLocation;
		this.status = status;
	}

	public MemberSubscription() {
		super();
	}
}