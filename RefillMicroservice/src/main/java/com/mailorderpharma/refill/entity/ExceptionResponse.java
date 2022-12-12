package com.mailorderpharma.refill.entity;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
/** Model class for the business details */
public class ExceptionResponse {

	/**
	 * Exception message
	 */
	String messge;
	/**
	 * Timestamp for the error message
	 */
	LocalDateTime timestamp;
	/**
	 * Http status
	 */
	HttpStatus status;

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