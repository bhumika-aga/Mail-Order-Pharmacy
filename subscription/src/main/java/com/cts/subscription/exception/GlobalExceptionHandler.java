package com.cts.subscription.exception;

import java.time.LocalDateTime;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.cts.subscription.model.ExceptionResponse;
import feign.RetryableException;

/**Class to handle all exceptions*/
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * @param invalidTokenException
	 * @return
	 */
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ExceptionResponse> invalidTokenException(InvalidTokenException invalidTokenException) {
		return new ResponseEntity<>(
				new ExceptionResponse(invalidTokenException.getMessage(), LocalDateTime.now(), HttpStatus.UNAUTHORIZED),
				HttpStatus.UNAUTHORIZED);
	}
	/**
	 * 
	 * @param stockNotFoundException
	 * @return
	 */
	@ExceptionHandler(StockNotFoundException.class)
	public ResponseEntity<ExceptionResponse> stockNotFoundException(StockNotFoundException stockNotFoundException) {
		return new ResponseEntity<>(
				new ExceptionResponse(stockNotFoundException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MicroServiceNotAvailable.class)
	public ResponseEntity<ExceptionResponse> microServiceUnavailableExceptions( ) {
		return new ResponseEntity<>(
				new ExceptionResponse("MicroServiceUnavailable", LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	/**
	 * @param invalidTokenException
	 * @return
	 */
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ExceptionResponse> nullPointerException(NullPointerException nullPointerException ) {
		return new ResponseEntity<>(
				new ExceptionResponse(nullPointerException.getMessage(), LocalDateTime.now(), HttpStatus.UNAUTHORIZED),
				HttpStatus.FAILED_DEPENDENCY);
	}
	
	/**
	 * @param drugNotFoundException
	 * @return
	 */
	@ExceptionHandler(DrugNotFoundException.class)
	public ResponseEntity<ExceptionResponse> drugNotFoundException(DrugNotFoundException drugNotFoundException) {
		return new ResponseEntity<>(
				new ExceptionResponse(drugNotFoundException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	/**
	 * 
	 * @param subscriptionListEmptyException
	 * @return
	 */
	@ExceptionHandler(SubscriptionListEmptyException.class)
	public ResponseEntity<ExceptionResponse> subscriptionListEmptyException(SubscriptionListEmptyException subscriptionListEmptyException) {
		return new ResponseEntity<>(
				new ExceptionResponse(subscriptionListEmptyException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	/**
	 * 
	 * @param subscriptionNotFoundException
	 * @return
	 */
	@ExceptionHandler(SubscriptionNotFoundException.class)
	public ResponseEntity<ExceptionResponse> subscriptionNotFoundException(SubscriptionNotFoundException subscriptionNotFoundException) {
		return new ResponseEntity<>(
				new ExceptionResponse(subscriptionNotFoundException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	/**
	 * @return  microServiceUnavailableException
	 */
	@ExceptionHandler(RetryableException.class)
	public ResponseEntity<ExceptionResponse> microServiceUnavailableException( ) {
		return new ResponseEntity<>(
				new ExceptionResponse("MicroServiceUnavailable", LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
}
