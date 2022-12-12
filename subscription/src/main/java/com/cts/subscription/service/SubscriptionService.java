package com.cts.subscription.service;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;

import com.cts.subscription.exception.DrugNotFoundException;
import com.cts.subscription.exception.InvalidTokenException;
import com.cts.subscription.exception.StockNotFoundException;
import com.cts.subscription.exception.SubscriptionListEmptyException;
import com.cts.subscription.exception.SubscriptionNotFoundException;
import com.cts.subscription.model.MemberPrescription;
import com.cts.subscription.model.MemberSubscription;

import feign.FeignException;

public interface SubscriptionService {

	/**
	 * 
	 * @param token
	 * @param prescription
	 * @return
	 * @throws InvalidTokenException
	 * @throws DrugNotFoundException
	 * @throws StockNotFoundException
	 */
	public ResponseEntity<String> subscribe(String token, MemberPrescription prescription)
			throws InvalidTokenException, FeignException, DrugNotFoundException;

	/**
	 * 
	 * @param toke
	 * @param id
	 * @return
	 * @throws SubscriptionNotFoundException
	 * @throws InvalidTokenException
	 */
	public ResponseEntity<MemberSubscription> getSubscription(String toke, long id)
			throws SubscriptionNotFoundException, InvalidTokenException;

	/**
	 * 
	 * @param token
	 * @param memberId
	 * @param subscriptionId
	 * @return
	 * @throws InvalidTokenException
	 */
	public ResponseEntity<String> unsubscribe(String token, String memberId, Long subscriptionId)
			throws InvalidTokenException,FeignException,EmptyResultDataAccessException;

	/**
	 * 
	 * @param token
	 * @param memberId
	 * @return
	 * @throws InvalidTokenException
	 * @throws SubscriptionListEmptyException
	 */
	public List<MemberSubscription> getAllSubscription(String token, String memberId)
			throws InvalidTokenException, SubscriptionListEmptyException;
	/**
	 * 
	 * @param token
	 * @param id
	 * @return
	 * @throws SubscriptionNotFoundException
	 * @throws InvalidTokenException
	 */
	public String getDrugBySubscription(String token, Long subscriptionId)
			throws SubscriptionNotFoundException, InvalidTokenException;
	
	
}
