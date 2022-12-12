package com.mailorderpharma.authservice.entity;

public class JwtResponse {

	private String jwt;

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public JwtResponse(String jwt) {
		super();
		this.jwt = jwt;
	}

	public JwtResponse() {
		super();
	}
}