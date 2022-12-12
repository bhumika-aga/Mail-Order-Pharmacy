package com.cts.subscription.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.OneToMany;

/** Model class for the business details */

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class DrugDetails {

	/**
	 * Id for the drug
	 */
	private String drugId;
	/**
	 * Name of the drug
	 */
	private String drugName;
	/**
	 * name of the manufacturer
	 */
	private String manufacturer;
	/**
	 * Manufacture date
	 */
	private Date manufactureDate;
	/**
	 * Expiry date
	 */
	private Date expiryDate;

	/**
	 * List of drug locations and quantities in location
	 */
	@OneToMany(mappedBy = "drugDetails")
	private List<DrugLocationDetails> druglocationQuantities = new ArrayList<>();

	public String getDrugId() {
		return drugId;
	}

	public void setDrugId(String drugId) {
		this.drugId = drugId;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Date getManufactureDate() {
		return manufactureDate;
	}

	public void setManufactureDate(Date manufactureDate) {
		this.manufactureDate = manufactureDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public List<DrugLocationDetails> getDruglocationQuantities() {
		return druglocationQuantities;
	}

	public void setDruglocationQuantities(List<DrugLocationDetails> druglocationQuantities) {
		this.druglocationQuantities = druglocationQuantities;
	}

	public DrugDetails(String drugId, String drugName, String manufacturer, Date manufactureDate, Date expiryDate,
			List<DrugLocationDetails> druglocationQuantities) {
		super();
		this.drugId = drugId;
		this.drugName = drugName;
		this.manufacturer = manufacturer;
		this.manufactureDate = manufactureDate;
		this.expiryDate = expiryDate;
		this.druglocationQuantities = druglocationQuantities;
	}

	public DrugDetails() {
		super();
	}
}