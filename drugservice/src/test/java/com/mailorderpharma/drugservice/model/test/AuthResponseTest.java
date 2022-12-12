package com.mailorderpharma.drugservice.model.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mailorderpharma.drugservice.entity.AuthResponse;

class AuthResponseTest {

	AuthResponse auth = new AuthResponse();
	AuthResponse auth2 = new AuthResponse("Uid","Name",true);
	@Test
	void testUid() {
		auth.setUid("Uid");
		assertEquals( "Uid", auth.getUid());
	}

	@Test
	void testName() {
		auth.setName("Name");
		assertEquals( "Name", auth.getName());
	}

	@Test
	void testIsValid() {
		auth.setValid(true);
		assertEquals( true, auth.isValid());
	}
	
	@Test
	void testToString() {
		String str = auth2.toString();
		assertEquals(auth2.toString(), str);
	}
}
