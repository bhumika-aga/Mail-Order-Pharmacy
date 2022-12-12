package com.cts.subscription.exception;

@SuppressWarnings("serial")
public class SubscriptionNotFoundException extends Exception {
	public SubscriptionNotFoundException()
	{
		
	}
	
	public SubscriptionNotFoundException(String message)
	{
		super(message);
	}

}
