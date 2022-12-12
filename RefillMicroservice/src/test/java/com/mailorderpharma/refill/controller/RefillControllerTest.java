package com.mailorderpharma.refill.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailorderpharma.refill.dao.RefillOrderRepository;
import com.mailorderpharma.refill.entity.MemberSubscription;
import com.mailorderpharma.refill.entity.RefillOrder;
import com.mailorderpharma.refill.entity.ValidateToken;
import com.mailorderpharma.refill.exception.InvalidTokenException;
import com.mailorderpharma.refill.exception.RefillEmptyException;
import com.mailorderpharma.refill.exception.SubscriptionIdNotFoundException;
import com.mailorderpharma.refill.restclients.AuthFeign;
import com.mailorderpharma.refill.restclients.SubscriptionClient;
import com.mailorderpharma.refill.service.RefillOrderService;
import com.mailorderpharma.refill.service.RefillOrderSubscriptionService;

@AutoConfigureMockMvc
@SpringBootTest
class RefillControllerTest {

	@InjectMocks
	RefillController refillController;

	@MockBean
	RefillOrderService refillOrderService;
	
	@MockBean
	RefillOrderSubscriptionService refillOrderSubscription;


	@Mock
	RefillOrderRepository repos;

	@MockBean
	AuthFeign authClient;
	
	@MockBean
	SubscriptionClient subscriptionClient;
	
	@Autowired
	MockMvc mockMvc;

	List<RefillOrder> list = new ArrayList<>();
	List<RefillOrder> finallist = null;

	@Test
	void viewRefillStatusTestByMemberId() throws Exception {
		list.add(new RefillOrder(1, LocalDate.now(), true, (long) 1, 10, "azeem"));
		list.add(new RefillOrder(2, LocalDate.now(), true, (long) 1, 5, "azeem"));
		when(repos.findAll()).thenReturn(list);

		finallist = list.stream().filter(p -> p.getMemberId().equals("azeem")).collect(Collectors.toList());

		repos.findAll().forEach(System.out::println);

		String expectedValue = list.get(0).getMemberId();
		System.out.println("Member ID =" + expectedValue);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		System.out.println(response);
		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(refillOrderService.getStatusByMember("azeem", "Bearer Token")).thenReturn(finallist);
		MvcResult result = mockMvc
				.perform(get("/api/viewRefillStatusforAll/{mid}", "azeem").header("Authorization", "Bearer Token"))
				.andReturn();
		String actualValue = result.getResponse().getContentAsString();
		System.out.println("Actual Value" + actualValue);
		assertEquals(expectedValue, actualValue.substring(186, 191));

	}

	@Test
	void viewRefillStatusBySubId() throws Exception {
		list.add(new RefillOrder(1, LocalDate.now(), true, (long) 1, 10, "azeem"));
		list.add(new RefillOrder(2, LocalDate.now(), true, (long) 1, 5, "azeem"));
		when(repos.findAll()).thenReturn(list);

		finallist = list.stream().filter(p -> p.getSubId() == 1).collect(Collectors.toList());

		repos.findAll().forEach(System.out::println);

		ObjectMapper objectMapper = new ObjectMapper();
		String expectedValue = objectMapper.writeValueAsString(list.get(0).getId());

		System.out.println("SUB ID =" + expectedValue);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		System.out.println(response);
		when(authClient.getValidity("Bearer Token")).thenReturn(response);
		when(refillOrderService.getStatus((long) 1, "Bearer Token")).thenReturn(finallist);
		MvcResult result = mockMvc
				.perform(get("/api/viewRefillStatus/{subid}", (long) 1).header("Authorization", "Bearer Token"))
				.andReturn();
		String actualValue = result.getResponse().getContentAsString();
		System.out.println("Actual Value" + actualValue);
		assertEquals(expectedValue, actualValue.substring(159, 160));

	}

	@Test
	void getRefillPaymentDuesSuccess() throws Exception {
		ArrayList<RefillOrder> list = new ArrayList<>();
		boolean finalValue;
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), false, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authClient.getValidity("token")).thenReturn(response);

		List<RefillOrder> paymentDueList = list.stream().filter(p -> p.getSubId() == 1).filter(p -> (!p.getPayStatus()))
				.collect(Collectors.toList());
		boolean expectedValue = paymentDueList.isEmpty() ? false : true;

		System.out.println("Expected Value  " + expectedValue);
		when(repos.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		when(refillOrderService.getRefillPaymentDues(1, "token")).thenReturn(expectedValue);
		MvcResult result = mockMvc.perform(get("/api/getRefillPaymentDues/{subscriptionId}",(long) 1)
				.header("Authorization", "token")).andReturn();
		String actualValue = result.getResponse().getContentAsString();
		
		if(actualValue.equals("true"))
			finalValue=true;
		else
			finalValue=false;
		System.out.println("Actual Value" + actualValue);
		assertEquals(expectedValue, finalValue);
	}
	
	
	@Test
	void getRefillPaymentDuesUnSuccess() throws Exception {
		ArrayList<RefillOrder> list = new ArrayList<>();
		boolean finalValue;
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authClient.getValidity("token")).thenReturn(response);

		List<RefillOrder> paymentDueList = list.stream().filter(p -> p.getSubId() == 1).filter(p -> (!p.getPayStatus()))
				.collect(Collectors.toList());
		boolean expectedValue = paymentDueList.isEmpty() ? false : true;

		System.out.println("Expected Value  " + expectedValue);
		when(repos.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		when(refillOrderService.getRefillPaymentDues(1, "token")).thenReturn(expectedValue);
		MvcResult result = mockMvc.perform(get("/api/getRefillPaymentDues/{subscriptionId}",(long) 1)
				.header("Authorization", "token")).andReturn();
		String actualValue = result.getResponse().getContentAsString();
		
		if(actualValue.equals("true"))
			finalValue=true;
		else
			finalValue=false;
		System.out.println("Actual Value" + actualValue);
		assertEquals(expectedValue, finalValue);
	}
	
	
	@Test
	void getall() throws Exception {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String expectedValue = objectMapper.writeValueAsString(list.get(0).getId());
		
		
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authClient.getValidity("token")).thenReturn(response);
	    when(refillOrderSubscription.getall("token")).thenReturn(list);
		MvcResult result = mockMvc
				.perform(get("/api/viewRefillOrderSubscriptionStatus").header("Authorization", "token"))
				.andReturn();
		String actual = result.getResponse().getContentAsString();
		assertEquals(expectedValue, actual.substring(7, 8));

	}
	@Test
	void deleteBySubscriptionId() throws SubscriptionIdNotFoundException, InvalidTokenException {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		RefillOrder refillOrder2 = new RefillOrder(1, LocalDate.now(), true, 2, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authClient.getValidity("token")).thenReturn(response);
		refillOrderSubscription.deleteBySubscriptionId(1, "token");
		int size = repos.findAll().size();
		assertEquals(list.size()-1, size);

	}
	
	/*
	 * @Test void getRefillDuesAsOfDate() throws Exception {
	 * ArrayList<MemberSubscription> memberList = new ArrayList<>();
	 * memberList.add(new MemberSubscription(1, 1, "azeem", LocalDate.now(), 10,
	 * "Paracetamol", 3, "Chennai", "true")); memberList.add(new
	 * MemberSubscription(2, 2, "azeem", LocalDate.now(), 20, "Crocin", 3,
	 * "Chennai", "true")); ArrayList<RefillOrder> list = new ArrayList<>();
	 * RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), false, 1, 3,
	 * "azeem"); RefillOrder refillOrder2 = new RefillOrder(1, LocalDate.now(),
	 * false, 2, 3, "azeem"); list.add(refillOrder); list.add(refillOrder2);
	 * 
	 * ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
	 * ResponseEntity<ValidateToken> response = new
	 * ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
	 * when(authClient.getValidity("token")).thenReturn(response);
	 * when(subscriptionClient.getAllSubscription("token",
	 * "azeem")).thenReturn(memberList);
	 * 
	 * when(repos.findBySubscriptionId(1L)).thenReturn(list);
	 * 
	 * //when(refillOrderService.getRefillDuesAsOfDate("azeem", "2021-03-12", //
	 * "token").thenReturn(expectedValue); MvcResult result =
	 * mockMvc.perform(get("/api/getRefillDuesAsOfDate/{memberId}/{date}","azeem",
	 * "2021-03-10") .header("Authorization", "token")).andReturn(); String
	 * actualValue = result.getResponse().getContentAsString();
	 * 
	 * 
	 * 
	 * 
	 * assertEquals(true,true); }
	 */
	
	
	
	/*
	 * List<DrugLocationDetails> list = new ArrayList<DrugLocationDetails>();
	 * 
	 * @Test void testGetDrugById() throws Exception { list.add(new
	 * DrugLocationDetails("D1", "Chennai", 30, null)); DrugDetails expected = new
	 * DrugDetails("D1", "Drug1", "manu1", new Date(), new Date(), list);
	 * ObjectMapper objectMapper = new ObjectMapper(); String expectedValue =
	 * objectMapper.writeValueAsString(expected).substring(11, 13);
	 * System.out.println("################################################"+
	 * expectedValue+"################################################"); TokenValid
	 * tokenValid = new TokenValid("uid", "uname", true); ResponseEntity<TokenValid>
	 * response = new ResponseEntity<TokenValid>(tokenValid, HttpStatus.OK);
	 * when(authFeign.getValidity("Bearer Token")).thenReturn(response);
	 * System.out.println("################################################"+
	 * response+"#######################");
	 * when(drugDetailsService.getDrugById("D1",
	 * "Bearer Token")).thenReturn(expected); MvcResult result =
	 * mockMvc.perform(get("/searchDrugsById/D1").header("Authorization",
	 * "Bearer Token")) .andReturn();
	 * System.out.println("################################################"+
	 * objectMapper.writeValueAsString(result.getResponse())+
	 * "################################################"); String actualValue =
	 * result.getResponse().getContentAsString().substring(11, 13);
	 * System.out.println("################################################"+
	 * actualValue+"################################################");
	 * assertEquals(expectedValue, actualValue); }
	 * 
	 */
	/*
	 * @Test void testupdateQuantity() throws Exception { TokenValid tokenValid =
	 * new TokenValid("uid", "uname", true); ObjectMapper objectMapper = new
	 * ObjectMapper(); ResponseEntity<SuccessResponse> expectedValue = new
	 * ResponseEntity<SuccessResponse>(new
	 * SuccessResponse("Refill done successfully"), HttpStatus.OK);
	 * ResponseEntity<TokenValid> response = new
	 * ResponseEntity<TokenValid>(tokenValid, HttpStatus.OK);
	 * when(authFeign.getValidity("Bearer Token")).thenReturn(response);
	 * when(drugDetailsService.updateQuantity("D1", "Chennai", 1,
	 * "Bearer token")).thenReturn(expectedValue);
	 * when(drugController.updateQuantity("D1", "Chennai", "Bearer token",
	 * 1)).thenReturn(expectedValue);
	 * 
	 * MvcResult result =
	 * mockMvc.perform(get("/updateDispatchableDrugStock/D1/Hyderabad/1").header(
	 * "Authorization", "Bearer Token")) .andReturn();
	 * 
	 * HttpStatus actualValue = response.getStatusCode();
	 * 
	 * HttpStatus exp = expectedValue.getStatusCode();
	 * 
	 * assertEquals(exp, actualValue); }
	 */

	/*
	 * @Test void viewRefillStatusTest() throws Exception {
	 * 
	 * // MvcResult result =
	 * mockMvc.perform(get("/viewRefillStatus/45")).andReturn(); //
	 * assertThrows(SubscriptionIdNotFoundException.class,
	 * ()->result.getResponse()); // Date date =new Date(); // RefillOrder
	 * refillOrder = new RefillOrder(1,date,true,1,1,"1"); // List<RefillOrder> list
	 * = new ArrayList<RefillOrder>(); // list.add(refillOrder); //
	 * //when(service.getStatus(1, "token")). ResponseEntity<List<RefillOrder>> s =
	 * refillController.viewRefillStatus("token", 1); String expected = "200 OK";
	 * String actual = s.getStatusCode().toString(); assertEquals(expected, actual);
	 */
	// }

	/*
	 * @Test public void viewRefillStatusByMemberId() throws InvalidTokenException,
	 * RefillEmptyException { ResponseEntity<List<RefillOrder>>
	 * viewRefillStatusByMemberId = refillController
	 * .viewRefillStatusByMemberId("token", "azeem"); String expected = "200 OK";
	 * String actual = viewRefillStatusByMemberId.getStatusCode().toString();
	 * assertEquals(expected, actual); }
	 */
	/*
	 * @Test void getRefillDuesAsOfDateTest() throws InvalidTokenException {
	 * ResponseEntity<List<RefillOrderSubscription>> s =
	 * refillController.getRefillDuesAsOfDate("token", "memberId", 45); String
	 * expected = "200 OK";
	 * 
	 * String actual = s.getStatusCode().toString();
	 * 
	 * assertEquals(expected, actual); }
	 */

	/*
	 * @Test void getRefillDuesAsOfPayment() throws InvalidTokenException {
	 * ResponseEntity<Boolean> s = refillController.getRefillPaymentDues("token",45
	 * ); String expected = "200 OK";
	 * 
	 * String actual = s.getStatusCode().toString();
	 * 
	 * assertEquals(expected, actual); }
	 * 
	 * @Test void requestAdhocRefill() throws InvalidTokenException, FeignException,
	 * ParseException, DrugQuantityNotAvailable { ResponseEntity<RefillOrder> s=
	 * refillController.requestAdhocRefill("token",54,true,45,"salem");
	 * 
	 * String expected = "202 ACCEPTED";
	 * 
	 * String actual = s.getStatusCode().toString();
	 * 
	 * assertEquals(expected, actual); }
	 * 
	 * @Test void requestRefillSubscription() throws InvalidTokenException,
	 * ParseException { ResponseEntity<RefillOrderSubscription> s =
	 * refillController.requestRefillSubscription("memberId", 4, "token", 45, 5);
	 * 
	 * String expected = "202 ACCEPTED";
	 * 
	 * String actual = s.getStatusCode().toString();
	 * 
	 * assertEquals(expected, actual); }
	 * 
	 * @Test void viewRefillOrderSubscriptionStatus() throws InvalidTokenException {
	 * ResponseEntity<List<RefillOrderSubscription>> s =
	 * refillController.viewRefillOrderSubscriptionStatus ("token"); String expected
	 * = "200 OK";
	 * 
	 * String actual = s.getStatusCode().toString();
	 * 
	 * assertEquals(expected, actual); }
	 * 
	 * @Test void startTimer() throws InvalidTokenException {
	 * refillController.startTimer("token"); String actual = "OK"; String expected =
	 * "OK"; assertEquals(actual, expected); }
	 * 
	 * @Test void deleteBySubscriptionId() throws InvalidTokenException {
	 * refillController.deleteBySubscriptionId("token",45); String actual = "OK";
	 * String expected = "OK"; assertEquals(actual, expected); }
	 * 
	 */
}