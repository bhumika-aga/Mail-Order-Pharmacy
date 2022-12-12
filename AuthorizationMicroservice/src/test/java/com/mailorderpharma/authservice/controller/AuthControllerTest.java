package com.mailorderpharma.authservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.mailorderpharma.authservice.dao.UserDAO;
import com.mailorderpharma.authservice.entity.AuthResponseEntity;
import com.mailorderpharma.authservice.entity.UserData;
import com.mailorderpharma.authservice.service.CustomerDetailsService;
import com.mailorderpharma.authservice.service.JwtUtil;

@SpringBootTest
class AuthControllerTest {

	@InjectMocks
	AuthController authController;

	AuthResponseEntity authResponse;

	UserDetails userdetails;

	@Mock
	JwtUtil jwtutil;

	@Mock
	CustomerDetailsService custdetailservice;

	@Mock
	UserDAO userservice;

	@Test
	 void loginTest() {

		UserData user = new UserData("admin", "admin",null,null);
		UserDetails loadUserByUsername = custdetailservice.loadUserByUsername("admin");
		UserDetails value = new User(user.getUserid(), user.getUpassword(), new ArrayList<>());
		when(custdetailservice.loadUserByUsername("admin")).thenReturn(value);
		when(jwtutil.generateToken(loadUserByUsername)).thenReturn("token");
		ResponseEntity<?> login = authController.login(user);
		assertEquals( 200, login.getStatusCodeValue());
	}

	@Test
	 void loginTestFailed() {

		UserData user = new UserData("admin", "admin",null,null);
		UserDetails loadUserByUsername = custdetailservice.loadUserByUsername("admin");
		UserDetails value = new User(user.getUserid(), "admin11", new ArrayList<>());
		when(custdetailservice.loadUserByUsername("admin")).thenReturn(value);
		when(jwtutil.generateToken(loadUserByUsername)).thenReturn("token");
		ResponseEntity<?> login = authController.login(user);
		assertEquals( 403, login.getStatusCodeValue());
	}

	@Test
	 void validateTestValidtoken() {

		when(jwtutil.validateToken("token")).thenReturn(true);
		when(jwtutil.extractUsername("token")).thenReturn("azeem");
		UserData user1 = new UserData("azeem", "azeem", "azeem",null);
		Optional<UserData> data = Optional.of(user1);
		when(userservice.findById("azeem")).thenReturn(data);
		ResponseEntity<?> validity = authController.getValidity("bearer token");
		assertEquals( true, validity.getBody().toString().contains("true"));

	}

	@Test
	 void validateTestInValidtoken() {

		
		when(jwtutil.validateToken("token")).thenReturn(false);
		ResponseEntity<?> validity = authController.getValidity("bearer token");
		assertEquals( true, validity.getBody().toString().contains("false"));
	}

	

}
