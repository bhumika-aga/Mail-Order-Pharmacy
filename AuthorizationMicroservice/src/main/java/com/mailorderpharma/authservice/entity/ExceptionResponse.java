package com.mailorderpharma.authservice.entity;

import java.time.LocalDateTime;

/** Model class for the business details */
public class ExceptionResponse {
	/**
	 * Response message
	 */
	String message;
	/**
	 * Response date
	 */
	LocalDateTime date;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public ExceptionResponse(String message, LocalDateTime date) {
		super();
		this.message = message;
		this.date = date;
	}

	public ExceptionResponse() {
		super();
	}

	@Override
	public String toString() {
		return "ExceptionResponse [message=" + message + ", date=" + date + "]";
	}
}