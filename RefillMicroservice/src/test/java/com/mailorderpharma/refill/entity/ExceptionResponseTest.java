package com.mailorderpharma.refill.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.mailorderpharma.refill.entity.ExceptionResponse;

class ExceptionResponseTest {

	LocalDateTime date = LocalDateTime.now();
	ExceptionResponse response1 = new ExceptionResponse();
	
		
	@Test
	void testMessage() {
		response1.setMessge("Success");
		assertEquals("Success", response1.getMessge());
	}
	
	@Test
	void testParameterizedMessage() {
		ExceptionResponse response1 = new ExceptionResponse("Success",LocalDateTime.now(),HttpStatus.OK);
		assertEquals("Success", response1.getMessge());
	}
	
	@Test
	void testDate() {
		response1.setTimestamp(LocalDateTime.now());
		assertEquals(LocalDateTime.now(), response1.getTimestamp());
	}
	
	@Test
	void testParameterizedDate() {
		ExceptionResponse response1 = new ExceptionResponse("Success",LocalDateTime.now(),HttpStatus.OK);
		assertEquals(LocalDateTime.now(),response1.getTimestamp());	}
	
	@Test
	void testHttpstatus() {
		response1.setStatus(HttpStatus.OK);
		assertEquals(HttpStatus.OK, response1.getStatus());
	}
	
	@Test
	void testParameterizedStatus() {
		ExceptionResponse response1 = new ExceptionResponse("Success",LocalDateTime.now(),HttpStatus.OK);
		assertEquals(HttpStatus.OK, response1.getStatus());
	}
}
