package com.mailorderpharma.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.mailorderpharma.authservice.dao.UserDAO;
import com.mailorderpharma.authservice.service.CustomerDetailsService;
import com.mailorderpharma.authservice.service.JwtUtil;

import io.jsonwebtoken.Jwts;

@SpringBootTest
 class JwUtilTest {

	@Mock
	UserDetails userdetails;

	@InjectMocks
	JwtUtil jwtUtil;

	@Mock
	UserDAO userservice;
	
	@Mock
	CustomerDetailsService customerDetailsService;
	

	@Test
	 void generateTokenTest() {
		userdetails = new User("azeem", "azeem", new ArrayList<>());
		String generateToken = jwtUtil.generateToken(userdetails);
		assertNotNull(generateToken);
	}

	@Test
	 void validateTokenTest() {
		userdetails = new User("azeem", "azeem", new ArrayList<>());
		String generateToken = jwtUtil.generateToken(userdetails);
		Boolean validateToken = jwtUtil.validateToken(generateToken);
		assertEquals(true, validateToken);
	}

	@Test
	 void validateTokenWithNameTest() {
		userdetails = new User("azeem", "azeem", new ArrayList<>());
		String generateToken = jwtUtil.generateToken(userdetails);
		Boolean validateToken = jwtUtil.validateToken(generateToken); 
		assertEquals(true, validateToken);
	}



}
