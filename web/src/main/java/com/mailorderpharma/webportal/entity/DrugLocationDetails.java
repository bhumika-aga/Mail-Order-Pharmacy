package com.mailorderpharma.webportal.entity;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

//@Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class DrugLocationDetails {

	@Id
	private String serialId;
	private String location;
	private int quantity;

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

	@Override
	public String toString() {
		return "DrugLocationDetails [serialId=" + serialId + ", location=" + location + ", quantity=" + quantity
				+ ", drugDetails=" + drugDetails + "]";
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