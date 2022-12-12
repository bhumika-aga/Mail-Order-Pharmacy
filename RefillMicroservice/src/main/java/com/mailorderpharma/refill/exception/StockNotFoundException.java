package com.mailorderpharma.refill.exception;

@SuppressWarnings("serial")
public class StockNotFoundException extends Exception {
	public StockNotFoundException(String message) {
		super(message);
	}
}
