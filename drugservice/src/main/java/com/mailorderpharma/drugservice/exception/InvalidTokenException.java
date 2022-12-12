package com.mailorderpharma.drugservice.exception;

@SuppressWarnings("serial")
public class InvalidTokenException extends Exception {

	
	public InvalidTokenException(String message){
		super(message);
	}
}
