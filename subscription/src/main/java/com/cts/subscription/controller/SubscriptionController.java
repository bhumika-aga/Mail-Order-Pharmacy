package com.cts.subscription.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.subscription.exception.DrugNotFoundException;
import com.cts.subscription.exception.InvalidTokenException;
import com.cts.subscription.exception.MicroServiceNotAvailable;
import com.cts.subscription.exception.StockNotFoundException;
import com.cts.subscription.exception.SubscriptionListEmptyException;
import com.cts.subscription.exception.SubscriptionNotFoundException;
import com.cts.subscription.model.MemberPrescription;
import com.cts.subscription.model.MemberSubscription;
import com.cts.subscription.service.SubscriptionService;

import feign.FeignException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api")
@RestController
public class SubscriptionController {

	private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);

	@Autowired
	SubscriptionService subscriptionService;

	String tokenMessage = "Invalid Token!!";
	String drugMessage = "Drug not found!!";

	/**
	 * http://localhost:9091/api/subscribe
	 * 
	 * @param token
	 * @param prescription
	 * @return
	 * @throws InvalidTokenException
	 * @throws DrugNotFoundException
	 * @throws MicroServiceNotAvailable
	 */
	@ApiOperation(value = "Subscribe to service", response = ResponseEntity.class)
	@PostMapping("/subscribe")
	public ResponseEntity<String> subscription(@RequestHeader("Authorization") String token,
			@RequestBody MemberPrescription prescription)
			throws InvalidTokenException, DrugNotFoundException, StockNotFoundException, MicroServiceNotAvailable {
		log.info("Start--Controller--subscription");
		try {

			return subscriptionService.subscribe(token, prescription);
		} catch (InvalidTokenException tokenException) {
			log.info("Catch--Controller--subscription");
			throw new InvalidTokenException(tokenMessage);
		}

		catch (FeignException e) {
			if (e.getMessage().contains("\"messge\":\"Drug Not Found\"")) {
				throw new DrugNotFoundException(drugMessage);
			}
			/*
			 * else { throw new
			 * StockNotFoundException("Stock Unavailable at your location"); }
			 * 
			 */
			else if (e.getMessage().contains("\"messge\":\"Stock Unavailable at your location\"")) {

				System.out.println("((((((((((((((((((((((((((((((((" + e.getMessage());
				throw new StockNotFoundException("Stock Unavailable at your location");
			} else {
				throw new MicroServiceNotAvailable("MicroService Not available");
			}

		}

	}

	/**
	 * 
	 * @param token
	 * @param memberId
	 * @param subscriptionId
	 * @return
	 * @throws InvalidTokenException
	 * @throws SubscriptionNotFoundException
	 * @throws MicroServiceNotAvailable
	 */
	@ApiOperation(value = "Unsubscribe to service", response = ResponseEntity.class)
	@PostMapping("/unsubscribe/{mid}/{id}")
	public ResponseEntity<String> unsubscribe(@RequestHeader("Authorization") String token,
			@PathVariable("mid") String memberId, @PathVariable("id") Long subscriptionId)
			throws InvalidTokenException, SubscriptionNotFoundException, MicroServiceNotAvailable {
		log.info("Start--Controller--unsubscribe");
		try {
			return subscriptionService.unsubscribe(token, memberId, subscriptionId);
		} catch (InvalidTokenException e) {
			log.info("Catch--Controller--unsubscribe");
			throw new InvalidTokenException(tokenMessage);
		} catch (EmptyResultDataAccessException e) {
			throw new SubscriptionNotFoundException("No subscription found to unsubscribe");
		} catch (FeignException e) {
			throw new MicroServiceNotAvailable("MicroService not available.Please try again");

		}

	}

	private Exception SubscriptionNotFoundException(String string) {
		return null;
	}

	/**
	 * 
	 * @param token
	 * @param id
	 * @return
	 * @throws SubscriptionNotFoundException
	 * @throws InvalidTokenException
	 */
	@ApiOperation(value = "Get One Subscription by subscription Id", response = ResponseEntity.class)
	@GetMapping("/getsubscription/{id}")
	public ResponseEntity<MemberSubscription> getSubscription(@RequestHeader("Authorization") String token,
			@PathVariable long id) throws SubscriptionNotFoundException, InvalidTokenException {
		try {
			log.info("Start--Controller--getSubscription");
			return subscriptionService.getSubscription(token, id);
		} catch (SubscriptionNotFoundException e) {
			log.info("Catch--Controller--getSubscription");
			throw new SubscriptionNotFoundException("Subscription Not found");
		} catch (InvalidTokenException e) {
			log.info("Catch--Controller--getSubscription");
			throw new InvalidTokenException(tokenMessage);
		}
	}

	/**
	 * 
	 * @param token
	 * @param id
	 * @return
	 * @throws SubscriptionNotFoundException
	 * @throws InvalidTokenException
	 */
	@ApiOperation(value = "Get Drug name by subscription Id", response = String.class)
	@GetMapping("/getdrugbysubscription/{id}")
	public String getDrugBySubscription(@RequestHeader("Authorization") String token, @PathVariable Long id)
			throws SubscriptionNotFoundException, InvalidTokenException {
		try {
			log.info("Start--Controller--getDrugBySubscription");
			return subscriptionService.getDrugBySubscription(token, id);
		} catch (SubscriptionNotFoundException e) {
			log.info("Catch--Controller--getDrugBySubscription");
			throw new SubscriptionNotFoundException("Subscription Not found");
		} catch (InvalidTokenException e) {
			log.info("Catch--Controller--getDrugBySubscription");
			throw new InvalidTokenException(tokenMessage);
		}
	}

	/**
	 * 
	 * @param token
	 * @param memberId
	 * @return
	 * @throws InvalidTokenException
	 * @throws SubscriptionListEmptyException
	 */
	@ApiOperation(value = "Get all subscription by member Id", response = List.class)
	@GetMapping("/getAllSubscriptions/{mid}")
	public List<MemberSubscription> getAllSubscription(@RequestHeader("Authorization") String token,
			@PathVariable("mid") String memberId) throws InvalidTokenException, SubscriptionListEmptyException {
		log.info("Start--Controller--getAllSubscription");
		try {

			return subscriptionService.getAllSubscription(token, memberId);
		} catch (InvalidTokenException e) {
			log.info("Catch--Controller--getAllSubscription");
			throw new InvalidTokenException(tokenMessage);
		} catch (SubscriptionListEmptyException e) {
			log.info("Catch--Controller--getAllSubscription");
			throw new SubscriptionListEmptyException("No subscription Found.Please subscribe");
		}

	}

}
