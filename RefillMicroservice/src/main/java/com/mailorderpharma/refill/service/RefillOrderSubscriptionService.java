package com.mailorderpharma.refill.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mailorderpharma.refill.entity.RefillOrder;
import com.mailorderpharma.refill.exception.InvalidTokenException;

/** Interface which have the methods of service class */
@Service
public interface RefillOrderSubscriptionService {

	
	

	public List<RefillOrder> getall(String token) throws InvalidTokenException;
	/**
	 * @param subscriptionId
	 * @param token
	 * @throws InvalidTokenException
	 */
	public void deleteBySubscriptionId(long subscriptionId, String token) throws InvalidTokenException;

}
