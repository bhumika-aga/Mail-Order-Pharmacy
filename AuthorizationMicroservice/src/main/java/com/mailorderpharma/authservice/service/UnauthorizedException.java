package com.mailorderpharma.authservice.service;

/**Service class*/
@SuppressWarnings("serial")
public class UnauthorizedException extends RuntimeException {
	/**
	 * @param message
	 */
	public UnauthorizedException(String message) {
		super(message);
	}

}
