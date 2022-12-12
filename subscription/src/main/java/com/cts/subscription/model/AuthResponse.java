package com.cts.subscription.model;

/** Model class for the business details */
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class AuthResponse {
	/**
	 * Id for user
	 */
	private String uid;
	/**
	 * Name of the user
	 */
	private String name;
	/**
	 * Validity check
	 */
	private boolean isValid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public AuthResponse(String uid, String name, boolean isValid) {
		super();
		this.uid = uid;
		this.name = name;
		this.isValid = isValid;
	}

	public AuthResponse() {
		super();
	}
}