package com.mailorderpharma.refill.exception;

/**Custom exception class*/
@SuppressWarnings("serial")
public class DrugQuantityNotAvailable extends Exception {
	
	/**
	 * @param message
	 */
	public  DrugQuantityNotAvailable(String message) {
		super(message);
	}
			
}
