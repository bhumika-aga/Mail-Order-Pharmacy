package com.mailorderpharma.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import com.mailorderpharma.authservice.dao.UserDAO;
import com.mailorderpharma.authservice.entity.UserData;
import com.mailorderpharma.authservice.service.CustomerDetailsService;


@SpringBootTest
 class ServiceTest {

	UserDetails userdetails;
	
	@InjectMocks
	CustomerDetailsService custdetailservice;

	@Mock
	UserDAO userservice;

	@Test
	 void loadUserByUsernameTest() {
		
		UserData user1=new UserData("azeem","azeem","azeem",null);
		Optional<UserData> data =Optional.of(user1) ;
		when(userservice.findById("azeem")).thenReturn(data);
		UserDetails loadUserByUsername2 = custdetailservice.loadUserByUsername("azeem");
		assertEquals(user1.getUserid(),loadUserByUsername2.getUsername());
	}

	
}
