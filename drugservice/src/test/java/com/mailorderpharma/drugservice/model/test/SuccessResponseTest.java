package com.mailorderpharma.drugservice.model.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mailorderpharma.drugservice.entity.ResponseForSuccess;

class SuccessResponseTest {

	@BeforeEach
	void setUp() throws Exception {
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		ResponseForSuccess successResponseOne = new ResponseForSuccess();
		successResponseOne.setResponseMessage("Success");
		assertEquals("Success", successResponseOne.getResponseMessage());
	}
	
	@Test
	void testAllArgs() {
		ResponseForSuccess successResponseOne = new ResponseForSuccess("Failure");
		assertEquals("Failure", successResponseOne.getResponseMessage());
	}

}
