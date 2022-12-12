package com.mailorderpharma.drugservice.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mailorderpharma.drugservice.entity.ResponseForException;

import feign.RetryableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ResponseForException> invalidTokenException(InvalidTokenException invalidTokenException) {
		return new ResponseEntity<>(
				new ResponseForException(invalidTokenException.getMessage(), LocalDateTime.now(), HttpStatus.UNAUTHORIZED),
				HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(DrugNotFoundException.class)
	public ResponseEntity<ResponseForException> drugNotFoundException(DrugNotFoundException drugNotFoundException) {
		return new ResponseEntity<>(
				new ResponseForException(drugNotFoundException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(StockNotFoundException.class)
	public ResponseEntity<ResponseForException> stockNotFoundException(StockNotFoundException stockNotFoundException) {
		return new ResponseEntity<>(
				new ResponseForException(stockNotFoundException.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	/**
	 * @return  microServiceUnavailableException
	 */
	@ExceptionHandler(RetryableException.class)
	public ResponseEntity<ResponseForException> microServiceUnavailableException( ) {
		return new ResponseEntity<>(
				new ResponseForException("MicroServiceUnavailable", LocalDateTime.now(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	
}
