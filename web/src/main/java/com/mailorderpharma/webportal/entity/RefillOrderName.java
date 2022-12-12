package com.mailorderpharma.webportal.entity;

import java.time.LocalDate;

/** Model class for the business details */
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
public class RefillOrderName {

	/**
	 * Refill id
	 */

	long id;
	/**
	 * Refill date
	 */
	private String drugName;

	LocalDate refilledDate;
	/**
	 * Pay status
	 */
	private Boolean payStatus;
	/**
	 * Subscription id
	 */
	private long subId;
	/**
	 * Quantity to refill
	 */
	int quantity;
	/**
	 * Member id
	 */
	String memberId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public LocalDate getRefilledDate() {
		return refilledDate;
	}

	public void setRefilledDate(LocalDate refilledDate) {
		this.refilledDate = refilledDate;
	}

	public Boolean getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Boolean payStatus) {
		this.payStatus = payStatus;
	}

	public long getSubId() {
		return subId;
	}

	public void setSubId(long subId) {
		this.subId = subId;
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

	@Override
	public String toString() {
		return "RefillOrderName [id=" + id + ", drugName=" + drugName + ", refilledDate=" + refilledDate
				+ ", payStatus=" + payStatus + ", subId=" + subId + ", quantity=" + quantity + ", memberId=" + memberId
				+ "]";
	}

	public RefillOrderName(long id, String drugName, LocalDate refilledDate, Boolean payStatus, long subId,
			int quantity, String memberId) {
		super();
		this.id = id;
		this.drugName = drugName;
		this.refilledDate = refilledDate;
		this.payStatus = payStatus;
		this.subId = subId;
		this.quantity = quantity;
		this.memberId = memberId;
	}

	public RefillOrderName() {
		super();
	}
}