package com.cts.subscription.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cts.subscription.client.AuthClient;
import com.cts.subscription.client.DrugClient;
import com.cts.subscription.client.RefillClient;
import com.cts.subscription.controller.SubscriptionController;
import com.cts.subscription.exception.DrugNotFoundException;
import com.cts.subscription.exception.InvalidTokenException;
import com.cts.subscription.exception.StockNotFoundException;
import com.cts.subscription.exception.SubscriptionListEmptyException;
import com.cts.subscription.exception.SubscriptionNotFoundException;
import com.cts.subscription.model.AuthResponse;
import com.cts.subscription.model.DrugDetails;
import com.cts.subscription.model.MemberPrescription;
import com.cts.subscription.model.MemberSubscription;
import com.cts.subscription.repository.MemberPrescriptionRepository;
import com.cts.subscription.repository.MemberSubscriptionRepository;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);

	@Autowired
	MemberSubscriptionRepository subscriptionRepository;

	@Autowired
	MemberPrescriptionRepository prescriptionRepository;

	@Autowired
	AuthClient authClient;

	@Autowired
	RefillClient refillClient;

	@Autowired
	DrugClient drugClient;

	String exceptionMessage = "Token is null";

	/**
	 * 
	 * @param token
	 * @param prescription
	 * @return
	 * @throws InvalidTokenException
	 * @throws DrugNotFoundException
	 * @throws StockNotFoundException
	 */
	@Override
	public ResponseEntity<String> subscribe(String token, MemberPrescription prescription)
			throws InvalidTokenException, FeignException, DrugNotFoundException {
		log.info("Start--ServiceImplementation--subscribe");
		MemberSubscription subscribe = new MemberSubscription();
		AuthResponse response = authClient.getValidity(token).getBody();
		DrugDetails drugDetails = null;
		if (response == null) {
			throw new NullPointerException(exceptionMessage);// i can add a custom exception
		}

		if (!response.isValid()) {
			throw new InvalidTokenException();
		}
		drugDetails = drugClient.getDrugByName(token, prescription.getDrugName());

		drugClient.updateQuantity(token, prescription.getDrugName(), prescription.getMemberLocation(),
				prescription.getQuantity());

		prescriptionRepository.save(prescription);

		subscribe.setDrugName(drugDetails.getDrugName());
		subscribe.setMemberId(prescription.getMemberId());
		subscribe.setMemberLocation(prescription.getMemberLocation());
		subscribe.setPrescriptionId(prescription.getId());
		subscribe.setQuantity(prescription.getQuantity());
		subscribe.setDate(LocalDate.now());
		subscribe.setStatus("active");
		subscribe.setRefillOccurrence(prescription.getCourseDuration());

		subscriptionRepository.save(subscribe);

		log.info("End--ServiceImplementation--subscribe");
		return new ResponseEntity<>("You have successfully subscribed to " + prescription.getDrugName(), HttpStatus.OK);

	}

	/**
	 * 
	 * @param toke
	 * @param id
	 * @return
	 * @throws SubscriptionNotFoundException
	 * @throws InvalidTokenException
	 */
	@Override
	public ResponseEntity<MemberSubscription> getSubscription(String token, long id)
			throws SubscriptionNotFoundException, InvalidTokenException {
		log.info("Start--ServiceImplementation--getSubscription");
		Optional<MemberSubscription> optional = subscriptionRepository.findById(id);
		AuthResponse response = authClient.getValidity(token).getBody();

		if (response == null)
			throw new NullPointerException(exceptionMessage);// i can add a custom exception

		if (!response.isValid())
			throw new InvalidTokenException();

		if (!optional.isPresent())
			throw new SubscriptionNotFoundException();

		log.info("End--ServiceImplementation--getSubscription");
		return new ResponseEntity<>(optional.get(), HttpStatus.OK);

	}

	/**
	 * 
	 * @param token
	 * @param memberId
	 * @param subscriptionId
	 * @return
	 * @throws InvalidTokenException
	 */
	@Override
	public String getDrugBySubscription(String token, Long id)
			throws SubscriptionNotFoundException, InvalidTokenException {

		log.info("Start--ServiceImplementation--getDrugBySubscription");
		Optional<MemberSubscription> optional = subscriptionRepository.findById(id);
		AuthResponse response = authClient.getValidity(token).getBody();

		if (response == null)
			throw new NullPointerException(exceptionMessage);// i can add a custom exception

		if (!response.isValid())
			throw new InvalidTokenException();

		if (!optional.isPresent())
			throw new SubscriptionNotFoundException();
		log.info("End--ServiceImplementation--getDrugBySubscription");
		return optional.get().getDrugName();

	}

	/**
	 * 
	 * @param token
	 * @param memberId
	 * @return
	 * @throws InvalidTokenException
	 * @throws SubscriptionListEmptyException
	 */
	@Override
	public ResponseEntity<String> unsubscribe(String token, String memberId, Long subscriptionId)
			throws InvalidTokenException, FeignException, EmptyResultDataAccessException {
		log.info("Start--ServiceImplementation--unsubscribe");
		AuthResponse response = authClient.getValidity(token).getBody();
		if (response == null)
			throw new NullPointerException(exceptionMessage);// i can add a custom exception

		if (!response.isValid())
			throw new InvalidTokenException();

		boolean result = refillClient.getRefillPaymentDues(token, subscriptionId);

		if (!result) {
			refillClient.deleteBySubscriptionId(token, subscriptionId);
			subscriptionRepository.deleteById(subscriptionId);
			log.info("End--ServiceImplementation--unsubscribe");
			return new ResponseEntity<>("You have succesfully Unsubscribed", HttpStatus.OK);
		} else {
			log.info("End--ServiceImplementation--unsubscribe");
			return new ResponseEntity<>("You have to clear your payment dues first.", HttpStatus.OK);
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
	@Override
	public List<MemberSubscription> getAllSubscription(String token, String memberId)
			throws InvalidTokenException, SubscriptionListEmptyException {

		log.info("Start--ServiceImplementation--getAllSubscription");
		AuthResponse response = authClient.getValidity(token).getBody();
		if (response == null) {
			throw new NullPointerException(exceptionMessage);// i can add a custom exception
		}

		if (!response.isValid()) {
			throw new InvalidTokenException();
		}

		if (subscriptionRepository.findByMemberId(memberId).isEmpty()) {
			throw new SubscriptionListEmptyException();
		}

		List<MemberSubscription> allDetails = subscriptionRepository.findByMemberId(memberId);
		log.info("End--ServiceImplementation--getAllSubscription");
		return allDetails;
	}

}
