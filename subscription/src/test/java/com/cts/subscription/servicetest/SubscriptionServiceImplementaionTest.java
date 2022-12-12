package com.cts.subscription.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.cts.subscription.client.AuthClient;
import com.cts.subscription.client.DrugClient;
import com.cts.subscription.client.RefillClient;
import com.cts.subscription.exception.DrugNotFoundException;
import com.cts.subscription.exception.InvalidTokenException;
import com.cts.subscription.exception.StockNotFoundException;
import com.cts.subscription.exception.SubscriptionNotFoundException;
import com.cts.subscription.model.AuthResponse;
import com.cts.subscription.model.DrugDetails;
import com.cts.subscription.model.DrugLocationDetails;
import com.cts.subscription.model.MemberPrescription;
import com.cts.subscription.model.MemberSubscription;
import com.cts.subscription.model.ResponseForSuccess;
import com.cts.subscription.repository.MemberPrescriptionRepository;
import com.cts.subscription.repository.MemberSubscriptionRepository;
import com.cts.subscription.service.SubscriptionServiceImpl;

@SpringBootTest(classes = SubscriptionServiceImplementaionTest.class)
class SubscriptionServiceImplementaionTest {

	@InjectMocks
	private SubscriptionServiceImpl subscriptionService;

	@Mock
	MemberPrescriptionRepository memberPrescriptionRepository;

	@Mock
	MemberSubscriptionRepository memberSubscriptionRepository;

	@Mock
	private AuthClient authClient;

	@Mock
	private DrugClient drugClient;

	@Mock
	private RefillClient refillClient;


	@Test
	void subscribe() throws InvalidTokenException, DrugNotFoundException, StockNotFoundException {
		List<DrugLocationDetails> list = new ArrayList<DrugLocationDetails>();
		list.add(new DrugLocationDetails("xy", "Chennai", 30, null));
		DrugDetails expected = new DrugDetails("xy", "Paracetamol", "Azeem", new Date(), new Date(), list);

		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Chennai", "6754",
				"MediBuddy", LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);
		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");

		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);

		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
		ResponseEntity<Object> res = new ResponseEntity<>(new ResponseForSuccess("Refill Done Successfully"),
				HttpStatus.OK);
		String resultExpected = "You have successfully subscribed to Paracetamol";

		when(authClient.getValidity("Bearer Token")).thenReturn(response);

		when(drugClient.getDrugByName("Bearer Token", "Paracetamol")).thenReturn(expected);

		when(drugClient.updateQuantity("Bearer Token", "Paracetamol", "Chennai", 3)).thenReturn(res);

		when(memberPrescriptionRepository.save(any(MemberPrescription.class))).thenReturn(memberPrescription);

		when(memberSubscriptionRepository.save(any(MemberSubscription.class))).thenReturn(memberSubscription);

		ResponseEntity<String> r = new ResponseEntity<String>(
				"You have successfully subscribed to " + memberPrescription.getDrugName(), HttpStatus.OK);
		String actual = subscriptionService.subscribe("Bearer Token", memberPrescription).getBody();
		assertEquals(resultExpected, actual);

	}

	@Test
	void getSubscriptionTest() throws Exception {

		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);

		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);

		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");

		Optional<MemberSubscription> optional = Optional.of(memberSubscription);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);

		assertEquals(memberSubscription.getSubscriptionId(),
				subscriptionService.getSubscription("Bearer Token", 1L).getBody().getSubscriptionId());
	}

	@Test
	void getDrugBySubscriptionTest() throws Exception {

		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);

		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);

		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");

		Optional<MemberSubscription> optional = Optional.of(memberSubscription);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);

		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);

		assertEquals(memberSubscription.getDrugName(), subscriptionService.getDrugBySubscription("Bearer Token", 1L));

	}

	@Test
	void getAllSubscriptionsTest() throws Exception {

		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);
		MemberPrescription memberPrescriptions = new MemberPrescription(12001L, "harshit", "Chennai", "6754",
				"MediBuddy", LocalDate.now(), "3times", 4, "Crocin", "Ayush", 4);
		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");
		MemberSubscription memberSubscriptions = new MemberSubscription(2L, memberPrescriptions.getId(),
				memberPrescriptions.getMemberId(), memberPrescriptions.getDate(), memberPrescriptions.getQuantity(),
				memberPrescriptions.getDrugName(), memberPrescriptions.getCourseDuration(),
				memberPrescriptions.getMemberLocation(), "active");
		List<MemberSubscription> list = new ArrayList<>();
		list.add(memberSubscription);
		list.add(memberSubscriptions);
		int expected = list.size();
		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(memberSubscriptionRepository.findByMemberId("harshit")).thenReturn(list);

		assertEquals(expected, subscriptionService.getAllSubscription("Bearer Token", "harshit").size());

	}

	@Test
	void unsubscribeTest() throws Exception {
		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);

		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");
		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
	
		String expected = "You have succesfully Unsubscribed";

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(refillClient.getRefillPaymentDues("Bearer Token", 1L)).thenReturn(false);

		
		ResponseEntity<String> actual = subscriptionService.unsubscribe("Bearer Token","harshit", 1L);

		assertEquals(expected,actual.getBody());
	}
	
	
	@Test
	void unsubscribeRefillDueTest() throws Exception {
		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);

		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");
		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
		
		String expected = "You have to clear your payment dues first.";

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(refillClient.getRefillPaymentDues("Bearer Token", 1L)).thenReturn(true);
		ResponseEntity<String> actual = subscriptionService.unsubscribe("Bearer Token","harshit", 1L);
		assertEquals(expected,actual.getBody());
		
	}

	@Test
	void getDrugBySubscriptionExceptionTest() throws Exception {

		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);

		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);

		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");

		Optional<MemberSubscription> optional = Optional.of(memberSubscription);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);

		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);

		assertThrows(SubscriptionNotFoundException.class,()->subscriptionService.getSubscription("Bearer Token", 2L),"getSubscriptionbyId");

	} 
	@Test
	void getSubscriptionTestException() throws Exception {

		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);

		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);

		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
				memberPrescription.getMemberLocation(), "active");

		Optional<MemberSubscription> optional = Optional.of(memberSubscription);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);

		assertThrows(SubscriptionNotFoundException.class,()->subscriptionService.getSubscription("Bearer Token", 2L),"getSubscriptionbyId");
		
	}

}
