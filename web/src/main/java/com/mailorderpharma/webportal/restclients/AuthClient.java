package com.mailorderpharma.webportal.restclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mailorderpharma.webportal.entity.AuthResponse;
import com.mailorderpharma.webportal.entity.UserData;

@FeignClient(name = "${authservice.client.name}",url = "${authservice.client.url}")
public interface AuthClient {
	
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public UserData login(@RequestBody UserData userlogincredentials);
	
	@RequestMapping(value = "/validate", method = RequestMethod.GET)
	public AuthResponse getValidity(@RequestHeader("Authorization") final String token);

}
