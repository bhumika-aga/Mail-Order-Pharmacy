package com.mailorderpharma.webportal.exceptions;

@SuppressWarnings("serial")
public class StockNotFoundException extends Exception {
	public StockNotFoundException(String message) {
		super(message);
	}
}
