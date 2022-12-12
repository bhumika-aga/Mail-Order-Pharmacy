package com.mailorderpharma.drugservice.model.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mailorderpharma.drugservice.entity.ValidateToken;

class TokenValidTest {

	ValidateToken token1 = new ValidateToken();
	ValidateToken token2 = new ValidateToken("Uid","Name",true);
	
	@Test
	void testUid() {
		token1.setUid("Uid");
		assertEquals( "Uid", token1.getUid());
	}

	@Test
	void testName() {
		token1.setName("Name");
		assertEquals( "Name", token1.getName());
	}

	@Test
	void testIsValid() {
		token1.setValid(true);
		assertEquals( true, token1.isValid());
	}
	
	@Test
	void testToString() {
		String str = token1.toString();
		assertEquals(token1.toString(), str);
	}

}
