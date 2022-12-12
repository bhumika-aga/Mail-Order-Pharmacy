package com.mailorderpharma.refill.service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mailorderpharma.refill.controller.RefillController;
import com.mailorderpharma.refill.dao.RefillOrderRepository;
import com.mailorderpharma.refill.entity.MemberSubscription;
import com.mailorderpharma.refill.entity.RefillOrder;
import com.mailorderpharma.refill.exception.DrugQuantityNotAvailable;
import com.mailorderpharma.refill.exception.InvalidTokenException;
import com.mailorderpharma.refill.exception.RefillEmptyException;
import com.mailorderpharma.refill.exception.SubscriptionIdNotFoundException;
import com.mailorderpharma.refill.exception.SubscriptionListEmptyException;
import com.mailorderpharma.refill.exception.SubscriptionNotFoundException;
import com.mailorderpharma.refill.restclients.AuthFeign;
import com.mailorderpharma.refill.restclients.DrugDetailClient;
import com.mailorderpharma.refill.restclients.SubscriptionClient;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/** Service class which holds the business logic */
@Service
@Slf4j
public class RefillOrderServiceImpl implements RefillOrderService {

	private static final Logger log = LoggerFactory.getLogger(RefillController.class);

	@Autowired
	public RefillOrderRepository refillOrderRepository;

	@Autowired
	RefillOrderSubscriptionServiceImpl refillOrderSubscriptionService;

	@Autowired
	DrugDetailClient drugDetailClient;

	@Autowired
	SubscriptionClient subscriptionClient;

	@Autowired
	private AuthFeign authFeign;

	String msg = "Invalid Credentials";

	/**
	 * @param subId
	 * @param token
	 * @return List<RefillOrder>
	 * @throws SubscriptionIdNotFoundException
	 * @throws InvalidTokenException
	 */
	@Override
	public List<RefillOrder> getStatus(long subId, String token)
			throws SubscriptionIdNotFoundException, InvalidTokenException {
		// get refill status
		log.info("inside getStatus method");
		if (authFeign.getValidity(token).getBody().isValid()) {
			ArrayList<RefillOrder> list = new ArrayList<>();
			List<RefillOrder> finallist = null;
			try {
				list = (ArrayList<RefillOrder>) refillOrderRepository.findAll();
				finallist = list.stream().filter(p -> p.getSubId() == subId).collect(Collectors.toList());
				// finallist.get(0);
			} catch (Exception ex) {
				throw new SubscriptionIdNotFoundException("Subscription ID is invalid");
			}
			return finallist;
		} else
			throw new InvalidTokenException(msg);

	}

	@Override
	public List<RefillOrder> getStatusByMember(String memberId, String token)
			throws InvalidTokenException, RefillEmptyException {
		// get refill status
		log.info("inside getStatus method");
		if (authFeign.getValidity(token).getBody().isValid()) {
			ArrayList<RefillOrder> list = new ArrayList<>();
			List<RefillOrder> finallist = null;
			try {
				list = (ArrayList<RefillOrder>) refillOrderRepository.findAll();
				finallist = list.stream().filter(p -> p.getMemberId().equals(memberId)).collect(Collectors.toList());
				finallist.get(0);
			} catch (Exception ex) {
				throw new RefillEmptyException("No refill for member");
			}
			return finallist;
		} else
			throw new InvalidTokenException(msg);

	}

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
	 */

	@Override
	public RefillOrder requestAdhocRefill(Long subId, Boolean payStatus, int quantity, String location, String token,
			String memberId) throws ParseException, FeignException, InvalidTokenException, DrugQuantityNotAvailable,
			SubscriptionNotFoundException {
		// request a on-the-go refill order
		log.info("inside requestAdhocRefill method");

		if (authFeign.getValidity(token).getBody().isValid()) {
			ResponseEntity<String> entityname = subscriptionClient.getDrugBySubscription(subId, token);

			String name = entityname.getBody();
			log.info("drugname ");

			// change this qs mark to appropriate type
			ResponseEntity<?> responseEntity = drugDetailClient.updateQuantity(token, name, location, quantity);
			log.info("updated");
			String responsevalues = responseEntity.getBody().toString();
			System.out.println("@@@@@@@@@@@@@@@@@@" + responseEntity);
			log.info("staus val");
			if (responsevalues.equalsIgnoreCase("{responseMessage=Refill Done Successfully}")) {
				RefillOrder refillOrder = new RefillOrder();
				refillOrder.setSubId(subId);
				LocalDate date = LocalDate.now();

				refillOrder.setRefilledDate(date);
				refillOrder.setQuantity(quantity);
				refillOrder.setPayStatus(payStatus);
				refillOrder.setMemberId(memberId);

				refillOrderRepository.save(refillOrder);
				log.info("refiloredr sabed");
				return refillOrder;
			} else {
				throw new DrugQuantityNotAvailable("DrugQuantityNotAvailable");

			}
		} else
			throw new InvalidTokenException(msg);
	}

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
	@Override
	public boolean getRefillPaymentDues(long subscriptionId, String token) throws InvalidTokenException {
		// check if there are any payment dues for a subscription
		log.info("inside getRefillDuesAsOfPayment method");

		if (authFeign.getValidity(token).getBody().isValid()) {
			List<RefillOrder> list = refillOrderRepository.findAll();

			List<RefillOrder> paymentDueList = list.stream().filter(p -> p.getSubId() == subscriptionId)
					.filter(p -> (!p.getPayStatus())).collect(Collectors.toList());

			if (paymentDueList.isEmpty()) {
				return false;
			} else {
				return true;
			}
		} else
			throw new InvalidTokenException(msg);

	}

	@Override
	public List<RefillOrder> getRefillDuesAsOfDate(String memberId, String date, String token)
			throws InvalidTokenException, SubscriptionListEmptyException {
		if (authFeign.getValidity(token).getBody().isValid()) {

			List<RefillOrder> allDues = new ArrayList<RefillOrder>();
			List<MemberSubscription> memberDetails = subscriptionClient.getAllSubscription(token, memberId);
			try {
				for (MemberSubscription memberSubscription : memberDetails) {
					// LocalDate subsDate=memberSubscription.getDate();
					int refillCycle = memberSubscription.getRefillOccurrence();
					LocalDate dueDate = memberSubscription.getDate().plusDays(refillCycle);

					if (LocalDate.parse(date).isBefore(dueDate)) {

						allDues.addAll(
								refillOrderRepository.findBySubscriptionId(memberSubscription.getSubscriptionId()));
						break;
					}

				}

				return allDues;
			} catch (DateTimeParseException e) {
				throw new DateTimeParseException("Wrong Format Received!!!", "", 0);
			}
		} else {
			throw new InvalidTokenException(msg);
		}
	}
}