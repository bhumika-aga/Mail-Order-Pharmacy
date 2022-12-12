package com.mailorderpharma.refill.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mailorderpharma.refill.entity.ValidateToken;

class ValidateTokenTest {

	ValidateToken token=new ValidateToken();
	
	@Test
	void testUid() {
		token.setUid("harshit");
		assertEquals( "harshit", token.getUid());
	}

	@Test
	void testName() {
		token.setName("harshit");
		assertEquals( "harshit", token.getName());
	}

	@Test
	void testIsValid() {
		token.setValid(true);
		assertEquals( true, token.isValid());
	}
	
	@Test
	void testToString() {
		String str = token.toString();
		assertEquals(token.toString(), str);
	}

}
