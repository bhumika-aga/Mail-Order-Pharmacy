package com.mailorderpharma.authservice.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mailorderpharma.authservice.controller.AuthController;
import com.mailorderpharma.authservice.entity.ExceptionResponse;

import lombok.extern.slf4j.Slf4j;

/** Service class */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomizedExceptionHandling extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	/**
	 * @param ex
	 * @return
	 */
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Object> handleUnauthorizedExceptions(UnauthorizedException ex) {

		logger.error("Unauthorized request cdfa");
		return ResponseEntity.badRequest()
				.body(new ExceptionResponse("Unauthorized request. Login again...", LocalDateTime.now()));
	}

	/**
	 * @param ex
	 * @return
	 */
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Object> handleNullPointerExceptions(Exception ex) {

		logger.error("User ID not available...............");
		return ResponseEntity.badRequest().body(new ExceptionResponse("User ID not available", LocalDateTime.now()));
	}

}