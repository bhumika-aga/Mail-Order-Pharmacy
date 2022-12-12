package com.mailorderpharma.webportal.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.OneToMany;

//@Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class DrugDetails {

	@Id
	private String drugId;
	private String drugName;
	private String manufacturer;
	private Date manufactureDate;
	private Date expiryDate;
	private String msg;
	@OneToMany(mappedBy = "drugDetails")
	private List<DrugLocationDetails> druglocationQuantities = new ArrayList<DrugLocationDetails>();

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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<DrugLocationDetails> getDruglocationQuantities() {
		return druglocationQuantities;
	}

	public void setDruglocationQuantities(List<DrugLocationDetails> druglocationQuantities) {
		this.druglocationQuantities = druglocationQuantities;
	}

	@Override
	public String toString() {
		return "DrugDetails [drugId=" + drugId + ", drugName=" + drugName + ", manufacturer=" + manufacturer
				+ ", manufactureDate=" + manufactureDate + ", expiryDate=" + expiryDate + ", msg=" + msg
				+ ", druglocationQuantities=" + druglocationQuantities + "]";
	}

	public DrugDetails(String drugId, String drugName, String manufacturer, Date manufactureDate, Date expiryDate,
			String msg, List<DrugLocationDetails> druglocationQuantities) {
		super();
		this.drugId = drugId;
		this.drugName = drugName;
		this.manufacturer = manufacturer;
		this.manufactureDate = manufactureDate;
		this.expiryDate = expiryDate;
		this.msg = msg;
		this.druglocationQuantities = druglocationQuantities;
	}

	public DrugDetails() {
		super();
	}
}