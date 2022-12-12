package com.mailorderpharma.drugservice.entity;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class ResponseForException {
	String messge;
	LocalDateTime timestamp;
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

	public ResponseForException(String messge, LocalDateTime timestamp, HttpStatus status) {
		super();
		this.messge = messge;
		this.timestamp = timestamp;
		this.status = status;
	}

	public ResponseForException() {
		super();
	}
}