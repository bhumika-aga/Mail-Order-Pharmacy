package com.cts.subscription.exceptionHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.naming.ServiceUnavailableException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.cts.subscription.exception.DrugNotFoundException;
import com.cts.subscription.exception.GlobalExceptionHandler;
import com.cts.subscription.exception.InvalidTokenException;
import com.cts.subscription.exception.StockNotFoundException;
import com.cts.subscription.exception.SubscriptionListEmptyException;
import com.cts.subscription.exception.SubscriptionNotFoundException;


@SpringBootTest
class GlobalExceptionHandlerTest {

	@InjectMocks
	GlobalExceptionHandler globalExceptionHandler;

	@Test
	void invalidTokenException() {
		assertEquals(HttpStatus.UNAUTHORIZED, globalExceptionHandler
				.invalidTokenException(new InvalidTokenException("invalidTokenException")).getStatusCode());
	}

	@Test
	void subscriptionListEmptyException() {
		assertEquals(HttpStatus.NOT_FOUND, globalExceptionHandler
				.subscriptionListEmptyException(new SubscriptionListEmptyException("SubscriptionListEmptyException"))
				.getStatusCode());
	}

	@Test
	void drugNotFoundException() {
		assertEquals(HttpStatus.NOT_FOUND, globalExceptionHandler
				.drugNotFoundException(new DrugNotFoundException("Drug not found")).getStatusCode());
	}

	@Test
	void subscriptionNotFoundException() {
		assertEquals(HttpStatus.NOT_FOUND,
				globalExceptionHandler
						.subscriptionNotFoundException(new SubscriptionNotFoundException("subscription not found"))
						.getStatusCode());
	}

	@Test
	void stockNotFoundException() {
		assertEquals(HttpStatus.NOT_FOUND, globalExceptionHandler
				.stockNotFoundException(new StockNotFoundException("stock not found")).getStatusCode());
	}

	@Test
	void microServiceNotFoundException() {
		assertEquals(HttpStatus.NOT_FOUND, globalExceptionHandler.microServiceUnavailableException().getStatusCode());
	}
	
	@Test
	void nullPointerException() {
		assertEquals(HttpStatus.FAILED_DEPENDENCY, globalExceptionHandler.nullPointerException(new NullPointerException()).getStatusCode());
	}


}
