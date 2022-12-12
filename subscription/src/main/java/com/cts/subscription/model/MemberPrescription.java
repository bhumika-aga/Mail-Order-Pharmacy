package com.cts.subscription.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
@Entity
public class MemberPrescription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String memberId;
	private String memberLocation;
	private String policyNumber;
	private String insuranceProvider;
	private LocalDate date;
	private String dosage;
	private int quantity;
	private String drugName;
	private String doctorDetails;
	private int courseDuration;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberLocation() {
		return memberLocation;
	}

	public void setMemberLocation(String memberLocation) {
		this.memberLocation = memberLocation;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getInsuranceProvider() {
		return insuranceProvider;
	}

	public void setInsuranceProvider(String insuranceProvider) {
		this.insuranceProvider = insuranceProvider;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
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

	public String getDoctorDetails() {
		return doctorDetails;
	}

	public void setDoctorDetails(String doctorDetails) {
		this.doctorDetails = doctorDetails;
	}

	public int getCourseDuration() {
		return courseDuration;
	}

	public void setCourseDuration(int courseDuration) {
		this.courseDuration = courseDuration;
	}

	public MemberPrescription(long id, String memberId, String memberLocation, String policyNumber,
			String insuranceProvider, LocalDate date, String dosage, int quantity, String drugName,
			String doctorDetails, int courseDuration) {
		super();
		this.id = id;
		this.memberId = memberId;
		this.memberLocation = memberLocation;
		this.policyNumber = policyNumber;
		this.insuranceProvider = insuranceProvider;
		this.date = date;
		this.dosage = dosage;
		this.quantity = quantity;
		this.drugName = drugName;
		this.doctorDetails = doctorDetails;
		this.courseDuration = courseDuration;
	}

	public MemberPrescription() {
		super();
	}
}