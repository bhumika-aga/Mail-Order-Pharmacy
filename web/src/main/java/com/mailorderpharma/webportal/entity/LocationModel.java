package com.mailorderpharma.webportal.entity;

import java.util.Date;

//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
public class LocationModel {
	Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "LocationModel [date=" + date + "]";
	}

	public LocationModel(Date date) {
		super();
		this.date = date;
	}

	public LocationModel() {
		super();
	}
}