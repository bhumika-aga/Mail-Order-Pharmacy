package com.mailorderpharma.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
 class CustomizedExceptionHandlingTest {
	
	@InjectMocks
	CustomizedExceptionHandling customizedExceptionHandling;
	
	@Mock
	UnauthorizedException unauthorizedException;
	
	
	
	@Test
	 void handleUnauthorizedExceptionsTest()
	{
		ResponseEntity<?> responseEntity= customizedExceptionHandling.handleUnauthorizedExceptions(unauthorizedException);
		assertEquals(400, responseEntity.getStatusCodeValue());
		
	}
	
	@Test
	 void handleNullPointerExceptions()
	{
		ResponseEntity<?> responseEntity= customizedExceptionHandling.handleNullPointerExceptions(null);
		assertEquals(400, responseEntity.getStatusCodeValue());
		
	}
	
	
	

}
