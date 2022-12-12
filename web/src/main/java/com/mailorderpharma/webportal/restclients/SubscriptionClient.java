package com.mailorderpharma.webportal.restclients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mailorderpharma.webportal.entity.PrescriptionDetails;
import com.mailorderpharma.webportal.entity.SubscriptionDetails;

@FeignClient(name = "${subscription.client.name}",url = "${subscription.client.url}")
public interface SubscriptionClient {

	@PostMapping("/subscribe")
	public String subscribe(@RequestHeader("Authorization") String token,
			@RequestBody PrescriptionDetails prescriptionDetails);
	
	@PostMapping("/unsubscribe/{mId}/{sId}")
	public String unsubscribe(@RequestHeader("Authorization") String token,
			@PathVariable("mId") String memberId, @PathVariable("sId") Long subscriptionId);
	
	@GetMapping("/getAllSubscriptions/{mId}")
	public List<SubscriptionDetails> getAllSubscription(@RequestHeader("Authorization") String token,
			@PathVariable("mId") String mId);

	@GetMapping("/getdrugbysubscription/{id}")
	public String getDrugBySubscription(@RequestHeader("Authorization") String token,
			@PathVariable Long id);
	@GetMapping("/getDrugName1/{sId}")
	public SubscriptionDetails getDrugBySubscriptionId(@RequestHeader("Authorization") String token,
			@PathVariable("sId") Long sId) ;
}
