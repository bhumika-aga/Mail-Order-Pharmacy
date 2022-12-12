package com.mailorderpharma.refill.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import javax.naming.ServiceUnavailableException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mailorderpharma.refill.entity.ExceptionResponse;

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
	
	@ExceptionHandler(DateTimeParseException.class)
	public ResponseEntity<ExceptionResponse> dateTimeParseException(DateTimeParseException invalidTokenException) {
		return new ResponseEntity<>(
				new ExceptionResponse(invalidTokenException.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST),
				HttpStatus.UNAUTHORIZED);
	}
	
	/**
	 * @param subscriptionException
	 * @return
	 */
	@ExceptionHandler(SubscriptionNotFoundException.class)
	public ResponseEntity<ExceptionResponse> subscriptionNotFoundException(SubscriptionNotFoundException subscriptionException) {
		return new ResponseEntity<>(
				new ExceptionResponse(subscriptionException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(SubscriptionListEmptyException.class)
	public ResponseEntity<ExceptionResponse> subscriptionListEmptyException(SubscriptionListEmptyException subscriptionException) {
		return new ResponseEntity<>(
				new ExceptionResponse(subscriptionException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(RefillEmptyException.class)
	public ResponseEntity<ExceptionResponse> refillEmptyException(RefillEmptyException refillEmptyException) {
		return new ResponseEntity<>(
				new ExceptionResponse(refillEmptyException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}


	/**
	 * @param serviceUnavailableException
	 * @return
	 */
	@ExceptionHandler(feign.RetryableException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ResponseEntity<ExceptionResponse>  serviceUnavailableException(ServiceUnavailableException serviceUnavailableException) {
		return new ResponseEntity<>(
				new ExceptionResponse("Temporarily service unavailable", LocalDateTime.now(),HttpStatus.SERVICE_UNAVAILABLE),HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	/**
	 * @param drugException
	 * @return
	 */
	@ExceptionHandler(DrugQuantityNotAvailable.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ResponseEntity<ExceptionResponse>  drugQuantityNotAvailable(DrugQuantityNotAvailable drugException) {
		return new ResponseEntity<>(
				new ExceptionResponse("DrugQuantityNotAvailable", LocalDateTime.now(),HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
	}
	
	/**
	 * @param drugException
	 * @return
	 */
	@ExceptionHandler(StockNotFoundException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ResponseEntity<ExceptionResponse>  stockNotFoundException(StockNotFoundException drugException) {
		return new ResponseEntity<>(
				new ExceptionResponse("Stock Not available at your location", LocalDateTime.now(),HttpStatus.NOT_FOUND),HttpStatus.NOT_FOUND);
	}
	
}
