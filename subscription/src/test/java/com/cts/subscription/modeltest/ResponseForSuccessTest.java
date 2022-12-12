package com.cts.subscription.modeltest;
import com.cts.subscription.model.ResponseForSuccess;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ResponseForSuccessTest {

	ResponseForSuccess response = new ResponseForSuccess();
	@Test
	void test() {
		response.setResponseMessage("This is a message");
		
		assertEquals("This is a message",response.getResponseMessage());
	}

}
