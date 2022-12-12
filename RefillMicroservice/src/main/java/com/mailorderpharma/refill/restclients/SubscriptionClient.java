package com.mailorderpharma.refill.restclients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mailorderpharma.refill.exception.SubscriptionListEmptyException;

/** Interface to connect with authentication service */
@FeignClient(name = "${subscription.client.name}", url = "${subscription.client.url}")
public interface SubscriptionClient {

	/**
	 * @param sId
	 * @param token
	 * @return
	 */
	@GetMapping("/getdrugbysubscription/{id}")
	public ResponseEntity<String> getDrugBySubscription(@PathVariable("id") Long sId,
			@RequestHeader("Authorization") String token);

	@GetMapping("/getAllSubscriptions/{mid}")
	public List<com.mailorderpharma.refill.entity.MemberSubscription> getAllSubscription(
			@RequestHeader("Authorization") String token, @PathVariable("mid") String memberId)
			throws com.mailorderpharma.refill.exception.InvalidTokenException, SubscriptionListEmptyException;
}