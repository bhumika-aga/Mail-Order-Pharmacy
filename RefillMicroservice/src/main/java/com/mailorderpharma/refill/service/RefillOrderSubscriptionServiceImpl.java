package com.mailorderpharma.refill.service;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mailorderpharma.refill.controller.RefillController;
import com.mailorderpharma.refill.dao.RefillOrderRepository;
import com.mailorderpharma.refill.entity.RefillOrder;
import com.mailorderpharma.refill.exception.InvalidTokenException;
import com.mailorderpharma.refill.restclients.AuthFeign;

import lombok.extern.slf4j.Slf4j;

/** Service class which holds the business logic */
@Service
@Slf4j
public class RefillOrderSubscriptionServiceImpl implements RefillOrderSubscriptionService {

	private static final Logger log = LoggerFactory.getLogger(RefillController.class);

	@Autowired
	RefillOrderRepository refillOrderRepository;

	@Autowired
	private AuthFeign authFeign;

	String msg = "Invalid Credentials";

	@Override
	public List<RefillOrder> getall(String token) throws InvalidTokenException {
		// get all refill order subscriptions
		log.info("inside getall method");

		if (authFeign.getValidity(token).getBody().isValid()) {
			return refillOrderRepository.findAll();
		} else
			throw new InvalidTokenException(msg);
	}

	/**
	 * @param subscriptionId
	 * @param token
	 * @throws InvalidTokenException
	 */
	@Override
	@Transactional
	public void deleteBySubscriptionId(long subscriptionId, String token) throws InvalidTokenException {
		// stop refill orders after delete subscription
		log.info("inside deleteBySubscriptionId method");

		if (authFeign.getValidity(token).getBody().isValid()) {
			refillOrderRepository.deleteBySubscriptionId(subscriptionId);
		} else
			throw new InvalidTokenException(msg);
	}
}