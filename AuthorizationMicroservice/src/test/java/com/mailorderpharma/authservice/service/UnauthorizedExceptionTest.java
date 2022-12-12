package com.mailorderpharma.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
 class UnauthorizedExceptionTest {
	
	
	
	@Test
	 void constructortest()
	{
		UnauthorizedException unauthorizedException =new UnauthorizedException("unauthorized");
		assertEquals("unauthorized", unauthorizedException.getMessage());
	}

}
