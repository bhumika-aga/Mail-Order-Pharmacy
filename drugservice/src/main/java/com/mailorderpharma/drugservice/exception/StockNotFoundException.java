package com.mailorderpharma.drugservice.exception;

@SuppressWarnings("serial")
public class StockNotFoundException extends Exception {
	public StockNotFoundException(String message) {
		super(message);
	}
}
