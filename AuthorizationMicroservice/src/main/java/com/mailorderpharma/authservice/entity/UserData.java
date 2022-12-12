package com.mailorderpharma.authservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** Model class for the business details */
@Entity(name = "user")
@Table
public class UserData {

	/**
	 * Id for user
	 */
	@Id
	@Column(name = "userid", length = 20)
	private String userid;
	/**
	 * Password for user
	 */
	@Column(name = "upassword", length = 20)
	private String upassword;
	/**
	 * Name for user
	 */
	@Column(name = "uname", length = 20)
	private String uname;
	/**
	 * Generated authentication token for the user
	 */
	private String authToken;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUpassword() {
		return upassword;
	}

	public void setUpassword(String upassword) {
		this.upassword = upassword;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public UserData(String userid, String upassword, String uname, String authToken) {
		super();
		this.userid = userid;
		this.upassword = upassword;
		this.uname = uname;
		this.authToken = authToken;
	}

	public UserData() {
		super();
	}

	@Override
	public String toString() {
		return "UserData [userid=" + userid + ", upassword=" + upassword + ", uname=" + uname + ", authToken="
				+ authToken + "]";
	}
}