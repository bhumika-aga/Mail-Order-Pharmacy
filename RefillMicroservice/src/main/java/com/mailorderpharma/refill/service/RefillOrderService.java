package com.mailorderpharma.refill.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mailorderpharma.refill.entity.RefillOrder;
import com.mailorderpharma.refill.exception.DrugQuantityNotAvailable;
import com.mailorderpharma.refill.exception.InvalidTokenException;
import com.mailorderpharma.refill.exception.RefillEmptyException;
import com.mailorderpharma.refill.exception.SubscriptionIdNotFoundException;
import com.mailorderpharma.refill.exception.SubscriptionListEmptyException;
import com.mailorderpharma.refill.exception.SubscriptionNotFoundException;

import feign.FeignException;

/** Interface which have the methods of service class */
@Service
public interface RefillOrderService {

	/**
	 * @param subId
	 * @param token
	 * @return List<RefillOrder>
	 * @throws SubscriptionIdNotFoundException
	 * @throws InvalidTokenException
	 */
	public List<RefillOrder> getStatus(long subId, String token)
			throws SubscriptionIdNotFoundException, InvalidTokenException;

	public List<RefillOrder> getStatusByMember(String memberId, String token)
			throws InvalidTokenException, RefillEmptyException;

	/**
	 * @param subId
	 * @param payStatus
	 * @param quantity
	 * @param location
	 * @param token
	 * @return RefillOrder
	 * @throws ParseException
	 * @throws FeignException
	 * @throws InvalidTokenException
	 * @throws DrugQuantityNotAvailable
	 * @throws SubscriptionNotFoundException
	 */
//	public RefillOrder requestAdhocRefill(Long subId, Boolean payStatus, int quantity, String location, String token,String memberId)
//			throws ParseException, FeignException, InvalidTokenException, DrugQuantityNotAvailable;

	public RefillOrder requestAdhocRefill(Long subId, Boolean payStatus, int quantity, String location, String token,
			String memberId) throws ParseException, FeignException, InvalidTokenException, DrugQuantityNotAvailable,
			SubscriptionNotFoundException;

	/**
	 * @param subId
	 * @param payStatus
	 * @param quantity
	 * @param location
	 * @param token
	 * @return boolean
	 * @throws ParseException
	 * @throws FeignException
	 * @throws InvalidTokenException
	 * @throws DrugQuantityNotAvailable
	 */
	public boolean getRefillPaymentDues(long subscriptionId, String token) throws InvalidTokenException;

	public List<RefillOrder> getRefillDuesAsOfDate(String memberId, String date, String token)
			throws InvalidTokenException, SubscriptionListEmptyException;
}