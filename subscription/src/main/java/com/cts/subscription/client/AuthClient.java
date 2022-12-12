package com.cts.subscription.client;


import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cts.subscription.model.AuthResponse;


@FeignClient(name = "${auth.client.name}",url = "${auth.client.url}")
public interface AuthClient {
	/**
	 * 
	 * @param token
	 * @return
	 */
	@GetMapping(value = "/validate")
	public ResponseEntity<AuthResponse> getValidity(@RequestHeader("Authorization") final String token);

}
