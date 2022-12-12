package com.cts.subscription.model;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

/** Model class for the business details */
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class ExceptionResponse {

	/**
	 * Message that the exception throws
	 */
	private String messge;
	/**
	 * Timestamp for the exception
	 */
	private LocalDateTime timestamp;
	/**
	 * Http status
	 */
	private HttpStatus status;

	public String getMessge() {
		return messge;
	}

	public void setMessge(String messge) {
		this.messge = messge;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public ExceptionResponse(String messge, LocalDateTime timestamp, HttpStatus status) {
		super();
		this.messge = messge;
		this.timestamp = timestamp;
		this.status = status;
	}

	public ExceptionResponse() {
		super();
	}
}