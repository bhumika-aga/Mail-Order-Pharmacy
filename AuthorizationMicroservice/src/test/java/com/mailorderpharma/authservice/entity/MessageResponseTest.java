package com.mailorderpharma.authservice.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
 class MessageResponseTest {
	
	ExceptionResponse msg = new ExceptionResponse();
	ExceptionResponse msg1 = new ExceptionResponse("error",LocalDateTime.now());
	
	@Test
	void testMsg() {
		msg.setMessage("Error Occured");
		assertEquals( "Error Occured", msg.getMessage());
	}

	
	@Test
	void testDate() {
		LocalDateTime date = LocalDateTime.now(); 
		msg.setDate(date);
		assertEquals( date, msg.getDate());
	}

	
	
	
	

}
