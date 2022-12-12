//package com.cts.subscription.controllertest;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
package com.cts.subscription.controllertest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.cts.subscription.client.AuthClient;
import com.cts.subscription.client.DrugClient;
import com.cts.subscription.client.RefillClient;
import com.cts.subscription.controller.SubscriptionController;
import com.cts.subscription.model.AuthResponse;
import com.cts.subscription.model.DrugDetails;
import com.cts.subscription.model.DrugLocationDetails;
import com.cts.subscription.model.MemberPrescription;
import com.cts.subscription.model.MemberSubscription;
import com.cts.subscription.model.ResponseForSuccess;
import com.cts.subscription.repository.MemberPrescriptionRepository;
import com.cts.subscription.repository.MemberSubscriptionRepository;
import com.cts.subscription.service.SubscriptionService;

@AutoConfigureMockMvc
@SpringBootTest
class RestControllerTest {

	@InjectMocks
	private SubscriptionController subscriptionController;

	@Mock
	private SubscriptionService subscriptionService;

	@MockBean
	MemberPrescriptionRepository memberPrescriptionRepository;

	@MockBean
	MemberSubscriptionRepository memberSubscriptionRepository;

	@MockBean
	private AuthClient authClient;

	@MockBean
	private DrugClient drugClient;

	@MockBean
	private RefillClient refillClient;

	@Autowired
	private MockMvc mockMvc;

	List<DrugLocationDetails> list = new ArrayList<DrugLocationDetails>();

	@Test
	void subscribeTest() throws Exception {

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
		ResponseEntity<String> subscriptionResponse = new ResponseEntity<String>(
				"You have successfully subscribed to " + memberPrescription.getDrugName(), HttpStatus.OK);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);

		when(drugClient.getDrugByName("Bearer Token", "Paracetamol")).thenReturn(expected);

		when(drugClient.updateQuantity("Bearer Token", "Paracetamol", "Chennai", 3)).thenReturn(res);

		when(memberPrescriptionRepository.save(any(MemberPrescription.class))).thenReturn(memberPrescription);
		
		when(memberSubscriptionRepository.save(any(MemberSubscription.class))).thenReturn(memberSubscription);
		
		when(subscriptionService.subscribe("Bearer Token", memberPrescription)).thenReturn(subscriptionResponse);

		MvcResult result = mockMvc.perform(post("/api/subscribe").header("Authorization", "Bearer Token")

				.contentType(MediaType.APPLICATION_JSON)
				.content(
						"{ \"courseDuration\": 1, \"date\": \"2021-02-02\", \"doctorDetails\": \"Gautam\", \"dosage\": \"2times\", \"drugName\": \"Paracetamol\", \"id\": 1, \"insuranceProvider\": \"MediBuddy\", \"memberId\": \"harshit\", \"memberLocation\": \"Chennai\", \"policyNumber\": \"6754\", \"quantity\": 3}")

				.accept(MediaType.APPLICATION_JSON))

				.andReturn();

		assertEquals(subscriptionResponse.getBody(), result.getResponse().getContentAsString());
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
		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(memberSubscriptionRepository.findByMemberId("harshit")).thenReturn(list);
		when(subscriptionService.getAllSubscription("Bearer Token", "harshit")).thenReturn(list);

		String expected = Long.toString(list.get(0).getSubscriptionId());

		expected = expected + Long.toString(list.get(1).getSubscriptionId());

		MvcResult result = mockMvc
				.perform(get("/api/getAllSubscriptions/harshit").header("Authorization", "Bearer Token")).andReturn();

		String actual = result.getResponse().getContentAsString().substring(19, 20);
		actual = actual + result.getResponse().getContentAsString().substring(205, 206);

		assertEquals(expected, actual);

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

		List<MemberSubscription> list = new ArrayList<MemberSubscription>();
		list.add(memberSubscription);

		Optional<MemberSubscription> optional = Optional.of(memberSubscription);

		when(authClient.getValidity("Bearer Token")).thenReturn(response);

		when(subscriptionService.getDrugBySubscription("Bearer Token", 1L)).thenReturn("Paracetamol");

		when(subscriptionController.getDrugBySubscription("Bearer Token", 1L)).thenReturn("Paracetamol");

		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);

		MvcResult result = mockMvc.perform(get("/api/getdrugbysubscription/1").header("Authorization", "Bearer Token"))
				.andReturn();

		assertEquals(optional.get().getDrugName(), result.getResponse().getContentAsString());

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
		ResponseEntity<MemberSubscription> responseEntity = new ResponseEntity<>(optional.get(), HttpStatus.OK);
		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(subscriptionService.getSubscription("Bearer Token", 1L)).thenReturn(responseEntity);
		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);
		when(subscriptionController.getSubscription("Bearer Token", 1L)).thenReturn(responseEntity);

		MvcResult result = mockMvc.perform(get("/api/getsubscription/1").header("Authorization", "Bearer Token"))
				.andReturn();

		String actual = result.getResponse().getContentAsString().substring(18, 19);

		assertEquals(Long.toString(optional.get().getSubscriptionId()), actual);

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
		ResponseEntity<String> responseEntity = new ResponseEntity<String>("You have succesfully Unsubscribed",
				HttpStatus.OK);
		String expected = "You have succesfully Unsubscribed";

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(refillClient.getRefillPaymentDues("Bearer Token", 1L)).thenReturn(false);
		when(subscriptionService.unsubscribe("Bearer Token", "harshit", 1L)).thenReturn(responseEntity);
		MvcResult result = mockMvc.perform(post("/api/unsubscribe/harshit/1").header("Authorization", "Bearer Token"))
				.andReturn();

		String actual = result.getResponse().getContentAsString();

		assertEquals(expected, actual);
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
		ResponseEntity<String> responseEntity = new ResponseEntity<String>("You have to clear your payment dues first.",
				HttpStatus.OK);
		String expected = "You have to clear your payment dues first.";

		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(refillClient.getRefillPaymentDues("Bearer Token", 1L)).thenReturn(true);
		when(subscriptionService.unsubscribe("Bearer Token", "harshit", 1L)).thenReturn(responseEntity);
		MvcResult result = mockMvc.perform(post("/api/unsubscribe/harshit/1").header("Authorization", "Bearer Token"))
				.andReturn();

		String actual = result.getResponse().getContentAsString();

		assertEquals(expected, actual);
	}

}

//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import com.cts.subscription.client.AuthClient;
//import com.cts.subscription.client.DrugClient;
//import com.cts.subscription.controller.SubscriptionController;
//import com.cts.subscription.exception.InvalidTokenException;
//import com.cts.subscription.exception.SubscriptionNotFoundException;
//import com.cts.subscription.model.AuthResponse;
//import com.cts.subscription.model.DrugDetails;
//import com.cts.subscription.model.DrugLocationDetails;
//import com.cts.subscription.model.MemberPrescription;
//import com.cts.subscription.model.MemberSubscription;
//import com.cts.subscription.model.ResponseForSuccess;
//import com.cts.subscription.repository.MemberPrescriptionRepository;
//import com.cts.subscription.repository.MemberSubscriptionRepository;
//import com.cts.subscription.service.SubscriptionService;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//class RestControllerTest {
//
//	@InjectMocks
//	private SubscriptionController subscriptionController;
//
//	
//	@Mock
//	private SubscriptionService subscriptionService;
//
//	@Mock
//	MemberPrescriptionRepository memberPrescriptionRepository;
//
//	@MockBean
//	MemberSubscriptionRepository memberSubscriptionRepository;
//
//	@MockBean
//	private AuthClient authClient;
//
//	@MockBean
//	private DrugClient drugClient;
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	List<DrugLocationDetails> list = new ArrayList<DrugLocationDetails>();
//
//	@Test
//	void subscribeTest() throws Exception {
//
//		list.add(new DrugLocationDetails("xy", "Chennai", 30, null));
//		DrugDetails expected = new DrugDetails("xy", "Paracetamol", "Azeem", new Date(), new Date(), list);
//
//		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Chennai", "6754",
//				"MediBuddy", LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);
//
//		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
//
//		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
//		when(authClient.getValidity("Bearer Token")).thenReturn(response);
//
//		when(drugClient.getDrugByName("Bearer Token", "Paracetamol")).thenReturn(expected);
//		ResponseEntity<Object> res = new ResponseEntity<>(new ResponseForSuccess("Refill Done Successfully"),
//				HttpStatus.OK);
//		when(drugClient.updateQuantity("Bearer Token", "Paracetamol", "Chennai", 3)).thenReturn(res);
//		ResponseEntity<String> subscriptionResponse = new ResponseEntity<String>(
//				"You have succesfully subscribed to " + memberPrescription.getDrugName(), HttpStatus.OK);
//		when(subscriptionService.subscribe("Bearer Token", memberPrescription)).thenReturn(subscriptionResponse);
//
//		MvcResult result = mockMvc.perform(post("/api/subscribe").header("Authorization", "Bearer Token")
//
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(
//						"{ \"courseDuration\": 1, \"date\": \"2021-02-02\", \"doctorDetails\": \"Gautam\", \"dosage\": \"2times\", \"drugName\": \"Paracetamol\", \"id\": 1, \"insuranceProvider\": \"MediBuddy\", \"memberId\": \"harshit\", \"memberLocation\": \"Chennai\", \"policyNumber\": \"6754\", \"quantity\": 3}")
//
//				.accept(MediaType.APPLICATION_JSON))
//
//				.andReturn();
//
//		String actual = "You have successfully subscribed to Paracetamol";
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$" + result.getResponse().getContentAsString());
//
//		assertEquals("hi", "hi");
//	}
//
////	@Test
////	
////	void subscribeTestException() throws Exception {
////
////		list.add(new DrugLocationDetails("xy", "Chennai", 30, null));
////		DrugDetails expected = new DrugDetails("xy", "Paracetamol", "Azeem", new Date(), new Date(), list);
////
////		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Chennai", "6754",
////				"MediBuddy", LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);
////
////		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
////
////		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
//////		Assertions.assertThrows(InvalidTokenException.class,when(authClient.getValidity("Bearer Token")).thenReturn(response));
////
////		when(drugClient.getDrugByName("Bearer Token", "Paracetamol")).thenReturn(expected);
////		ResponseEntity<Object> res = new ResponseEntity<>(new ResponseForSuccess("Refill Done Successfully"),
////				HttpStatus.OK);
////		when(drugClient.updateQuantity("Bearer Token", "Paracetamol", "Chennai", 3)).thenReturn(res);
////		ResponseEntity<String> subscriptionResponse = new ResponseEntity<String>(
////				"You have succesfully subscribed to " + memberPrescription.getDrugName(), HttpStatus.OK);
////		when(subscriptionService.subscribe("Bearer Token", memberPrescription)).thenReturn(subscriptionResponse);
////
////		//Assertions.assertThrows(expectedType, executable)
////
////		MvcResult result = mockMvc.perform(post("/api/subscribe").header("Authorization", "Bearer Token")
////
////				.contentType(MediaType.APPLICATION_JSON)
////				.content(
////						"{ \"courseDuration\": 1, \"date\": \"2021-02-02\", \"doctorDetails\": \"Gautam\", \"dosage\": \"2times\", \"drugName\": \"Paracetamol\", \"id\": 1, \"insuranceProvider\": \"MediBuddy\", \"memberId\": \"harshit\", \"memberLocation\": \"Chennai\", \"policyNumber\": \"6754\", \"quantity\": 3}")
////
////				.accept(MediaType.APPLICATION_JSON))
////
////				.andReturn();
////
////		 System.out.println("$$$$$$$$$$$$$$$$$$$$$"+result.getResponse().getContentAsString());
////
////		assertEquals("hi", "hi");
////	}
//
////	@Test
////	void getAllSubscriptionsTest() throws Exception {
////
////		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
////				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);
////		MemberPrescription memberPrescriptions = new MemberPrescription(12001L, "harshit", "Chennai", "6754", "MediBuddy",
////				LocalDate.now(), "3times", 4, "Crocin", "Ayush", 4);
////		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
////				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
////				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
////				memberPrescription.getMemberLocation(), "active");
////		MemberSubscription memberSubscriptions = new MemberSubscription(2L, memberPrescriptions.getId(),
////				memberPrescriptions.getMemberId(), memberPrescriptions.getDate(), memberPrescriptions.getQuantity(),
////				memberPrescriptions.getDrugName(), memberPrescriptions.getCourseDuration(),
////				memberPrescriptions.getMemberLocation(), "active");
////		List<MemberSubscription> list = new ArrayList<>();
////		list.add(memberSubscription);
////		list.add(memberSubscriptions);
////		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
////		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
////		when(authClient.getValidity("Bearer Token")).thenReturn(response);
////		
//////		when(memberSubscriptionRepository.findByMemberId("harshit").isEmpty()).thenReturn(false);
////		when(memberSubscriptionRepository.findByMemberId("harshit")).thenReturn(list);
////		when(subscriptionService.getAllSubscription("Bearer Token", "harshit")).thenReturn(list);
////		
////		MvcResult result = mockMvc.perform(get("/api/getAllSubscriptions/harshit").header("Authorization", "Bearer Token")).andReturn();
////
////		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$"+result.getResponse().getContentLength());
////		assertEquals("hi","hi");
////
////	}
//
//	@Test
//	void getDrugBySubscriptionTest() throws Exception {
//
//		AuthResponse authResponse = new AuthResponse("harshit", "harshit", true);
//
//		ResponseEntity<AuthResponse> response = new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
//	
//
//		MemberPrescription memberPrescription = new MemberPrescription(12001L, "harshit", "Pune", "6754", "MediBuddy",
//				LocalDate.now(), "2times", 3, "Paracetamol", "Gautam", 3);
//		
//		MemberSubscription memberSubscription = new MemberSubscription(1L, memberPrescription.getId(),
//				memberPrescription.getMemberId(), memberPrescription.getDate(), memberPrescription.getQuantity(),
//				memberPrescription.getDrugName(), memberPrescription.getCourseDuration(),
//				memberPrescription.getMemberLocation(), "active");
//		
//		List<MemberSubscription> list = new ArrayList<MemberSubscription>();
//		list.add(memberSubscription);
//		
//		Optional<MemberSubscription> optional =Optional.ofNullable(memberSubscription);
//		
//		
//		when(authClient.getValidity("Bearer Token")).thenReturn(response);
//		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);
//	//	System.out.println(" @@@@@@@@@@@@@ "+optional.get().getDrugName());
//		
//		
//		when(memberSubscriptionRepository.findAll()).thenReturn(list);
//		when(memberSubscriptionRepository.findById(1L)).thenReturn(optional);
//		//when(memberSubscriptionRepository.getOne(1L)).thenReturn(memberSubscription);
//		when(subscriptionService.getDrugBySubscription("Bearer Token",1L)).thenReturn("Paracetamol");
//		
//		MvcResult result = mockMvc.perform(get("/api/getdrugbysubscription/1").header("Authorization", "Bearer Token")).andReturn();
//		
//		
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+memberSubscriptionRepository.findAll());
//		//System.out.println("(((((((((((((((((((((((((((((((((("+memberSubscriptionRepository.get);
//		System.out.println("^^^^^^^^^^^^^^^^^^^^^"+result.getResponse().getContentAsString());
//		System.out.println("^^^^^^^^^^^^^^^^^^^^^"+result.getResponse().getContentLength());
//		
//		assertEquals("hi","hi");
//		
//		
////		ResponseEntity<String> res = new ResponseEntity<String>(
////				"You have succesfully subscribed to - " + memberPrescription.getDrugName(), HttpStatus.OK);
////		when(subscriptionService.subscribe("Bearer Token", memberPrescription)).thenReturn(res);
////		ResponseEntity<String> drugname = new ResponseEntity<String>(memberPrescription.getDrugName(), HttpStatus.OK);
////		when(subscriptionService.getDrugNameBySubscriptionId(memberPrescription.getPrescriptionId(), "Bearer Token"))
////				.thenReturn(drugname);
////		when(subscriptionController.getDrugNameBySubscriptionId("Bearer Token", memberPrescription.getPrescriptionId()))
////				.thenReturn(drugname);
////
////		String actual = "<200 OK OK,You have succesfully subscribed to - Drug1,[]>";
////		assertEquals(res.toString(), actual);
//
//	}
////	  
////	  @Test void unsubscribeTest() { PrescriptionDetails prescriptionDetails = new
////	  PrescriptionDetails(12001L, "admin", "chennai", "12001", "chennai",
////	  LocalDate.now(), "Drug1", "weekly", 1, 3, "prakash"); ResponseEntity<String>
////	  res = new ResponseEntity<String>( "You have succesfully subscribed to " +
////	  prescriptionDetails.getDrugName(), HttpStatus.OK);
////	  when(subscriptionService.subscribe(prescriptionDetails,
////	  "Bearer Token")).thenReturn(res); ResponseEntity<String> response = new
////	  ResponseEntity<String>("You have succesfully Unsubscribed", HttpStatus.OK);
////	  when(subscriptionService.unsubscribe(prescriptionDetails.getMemberId(),
////	  prescriptionDetails.getPrescriptionId(),
////	  "Bearer Token")).thenReturn(response); assertEquals( 200,
////	  subscriptionController .unsubscribe("Bearer Token",
////	  prescriptionDetails.getMemberId(), prescriptionDetails.getPrescriptionId())
////	  .getStatusCodeValue()); }
////	 
//}
