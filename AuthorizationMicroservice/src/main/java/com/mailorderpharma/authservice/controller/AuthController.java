package com.mailorderpharma.authservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailorderpharma.authservice.dao.UserDAO;
import com.mailorderpharma.authservice.entity.AuthResponseEntity;
import com.mailorderpharma.authservice.entity.UserData;
import com.mailorderpharma.authservice.service.CustomerDetailsService;
import com.mailorderpharma.authservice.service.JwtUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
public class AuthController {

	@Autowired
	private JwtUtil jwtutil;
	@Autowired
	private CustomerDetailsService custdetailservice;
	@Autowired
	private UserDAO userservice;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@ApiOperation(value = "Verify the credentials and generate JWT Token", response = ResponseEntity.class)
	@PostMapping(value = "/login")
	public ResponseEntity<Object> login(@RequestBody UserData userlogincredentials) {
		// Generates token for login
		final UserDetails userdetails = custdetailservice.loadUserByUsername(userlogincredentials.getUserid());
		String uid = "";
		String generateToken = "";
		if (userdetails.getPassword().equals(userlogincredentials.getUpassword())) {
			uid = userlogincredentials.getUserid();
			generateToken = jwtutil.generateToken(userdetails);
			return new ResponseEntity<>(new UserData(uid, null, null, generateToken), HttpStatus.OK);
		} else {
			LOGGER.info("At Login : ");
			LOGGER.error("Invalid Login Credentials!!!");
			return new ResponseEntity<>("Invalid Login Credentials!!!", HttpStatus.FORBIDDEN);
		}
	}

	@ApiOperation(value = "Validate JWT Token", response = ResponseEntity.class)
	@GetMapping(value = "/validate")
	public ResponseEntity<Object> getValidity(@RequestHeader("Authorization") final String token) {

		// Returns response after Validating received token

		String token1 = token.substring(7);
		AuthResponseEntity res = new AuthResponseEntity();
		if (jwtutil.validateToken(token1)) {
			res.setUid(jwtutil.extractUsername(token1));
			res.setValid(true);
			res.setName(userservice.findById(jwtutil.extractUsername(token1)).get().getUname());
		} else {
			res.setValid(false);
			LOGGER.info("At Validity : ");
			LOGGER.error("Token Has Expired");
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
}