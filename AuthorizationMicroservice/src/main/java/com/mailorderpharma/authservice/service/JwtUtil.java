package com.mailorderpharma.authservice.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**Service class*/
@Service
public class JwtUtil {

	private String secretkey = "${jwt.secret}";

	/**
	 * @param token
	 * @return
	 */
	public String extractUsername(String token) {
		
		return extractClaim(token, Claims::getSubject);
				
	}

	/**
	 * @param <T>
	 * @param token
	 * @param claimsResolver
	 * @return
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	
	}

	/**
	 * @param token
	 * @return
	 */
	private Claims extractAllClaims(String token) {
		
			return Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody();
			
	}

	/**
	 * @param userDetails
	 * @return
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());
	}

	/**
	 * @param claims
	 * @param subject
	 * @return
	 */
	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 6000000)) // token for 10 minute
				.signWith(SignatureAlgorithm.HS256, secretkey).compact();
		/**return compact;*/
	}

	/**
	 * @param token
	 * @return
	 */
	public Boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretkey).parseClaimsJws(token).getBody();
			return true;
		}
		catch(Exception e) {
			return false;
		}
	
	}
}