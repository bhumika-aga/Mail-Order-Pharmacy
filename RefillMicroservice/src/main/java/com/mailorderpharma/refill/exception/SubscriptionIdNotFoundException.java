package com.mailorderpharma.refill.exception;

/**Custom exception class*/
@SuppressWarnings("serial")
public class SubscriptionIdNotFoundException extends Exception {
	
	/**
	 * @param message
	 */
	public SubscriptionIdNotFoundException(String message) {
		super(message);
	}
			
}
