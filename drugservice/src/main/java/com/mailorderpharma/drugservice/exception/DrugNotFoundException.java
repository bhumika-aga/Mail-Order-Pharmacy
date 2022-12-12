package com.mailorderpharma.drugservice.exception;

@SuppressWarnings("serial")
public class DrugNotFoundException extends RuntimeException{

	public DrugNotFoundException(String message) {
		super(message);
	}
}
