package com.mailorderpharma.drugservice.entity;

import java.util.Date;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class Stock {
	private String drugId;
	private String drugName;
	private Date expiryDate;
	private int stocks;

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

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public int getStocks() {
		return stocks;
	}

	public void setStocks(int stocks) {
		this.stocks = stocks;
	}

	public Stock(String drugId, String drugName, Date expiryDate, int stocks) {
		super();
		this.drugId = drugId;
		this.drugName = drugName;
		this.expiryDate = expiryDate;
		this.stocks = stocks;
	}

	public Stock() {
		super();
	}
}