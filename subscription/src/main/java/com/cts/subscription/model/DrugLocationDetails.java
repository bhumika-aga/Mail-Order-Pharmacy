package com.cts.subscription.model;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Model class for the business details */

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class DrugLocationDetails {

	/**
	 * Serial id for location
	 */
	private String serialId;
	/**
	 * Location name
	 */
	private String location;
	/**
	 * Quantity per location
	 */
	private int quantity;

	/**
	 * Object of drug class containing drug details
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	@JoinColumn(name = "drugId")
	private DrugDetails drugDetails;

	public String getSerialId() {
		return serialId;
	}

	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public DrugDetails getDrugDetails() {
		return drugDetails;
	}

	public void setDrugDetails(DrugDetails drugDetails) {
		this.drugDetails = drugDetails;
	}

	public DrugLocationDetails(String serialId, String location, int quantity, DrugDetails drugDetails) {
		super();
		this.serialId = serialId;
		this.location = location;
		this.quantity = quantity;
		this.drugDetails = drugDetails;
	}

	public DrugLocationDetails() {
		super();
	}
}