package com.mailorderpharma.refill.entity;

/** Model class for the business details */
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
public class ValidateToken {
	/**
	 * User id
	 */
	private String uid;
	/**
	 * User name
	 */
	private String name;
	/**
	 * Validity check for the token
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

	@Override
	public String toString() {
		return "ValidateToken [uid=" + uid + ", name=" + name + ", isValid=" + isValid + "]";
	}

	public ValidateToken(String uid, String name, boolean isValid) {
		super();
		this.uid = uid;
		this.name = name;
		this.isValid = isValid;
	}

	public ValidateToken() {
		super();
	}
}