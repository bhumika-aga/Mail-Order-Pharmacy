package com.cts.subscription.exception;

@SuppressWarnings("serial")
public class DrugNotFoundException extends Exception {

	public DrugNotFoundException()
	{
		//Default Constructor
	}
	
	public DrugNotFoundException(String message)
	{
		super(message);
	}



}
