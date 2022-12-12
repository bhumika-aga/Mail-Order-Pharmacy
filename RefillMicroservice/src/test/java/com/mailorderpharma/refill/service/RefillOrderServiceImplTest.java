package com.mailorderpharma.refill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mailorderpharma.refill.dao.RefillOrderRepository;
import com.mailorderpharma.refill.entity.MemberSubscription;
import com.mailorderpharma.refill.entity.RefillOrder;
import com.mailorderpharma.refill.entity.ValidateToken;
import com.mailorderpharma.refill.exception.DrugQuantityNotAvailable;
import com.mailorderpharma.refill.exception.InvalidTokenException;
import com.mailorderpharma.refill.exception.RefillEmptyException;
import com.mailorderpharma.refill.exception.SubscriptionIdNotFoundException;
import com.mailorderpharma.refill.exception.SubscriptionListEmptyException;
import com.mailorderpharma.refill.restclients.AuthFeign;
import com.mailorderpharma.refill.restclients.DrugDetailClient;
import com.mailorderpharma.refill.restclients.SubscriptionClient;
import com.sun.el.parser.ParseException;

import feign.FeignException;

@SpringBootTest(classes = RefillOrderServiceImplTest.class)
class RefillOrderServiceImplTest {

	@InjectMocks
	RefillOrderServiceImpl refillOrderServiceImpl;

	@Mock
	RefillOrderSubscriptionServiceImpl refillOrderSubscriptionService;

	@Mock
	DrugDetailClient drugDetailClient;

	@Mock
	SubscriptionClient subscriptionClient;

	@Mock
	RefillOrderRepository refillOrderRepository;

	@Mock
	private AuthFeign authFeign;

	@Test
	void getStatusBySubId() throws SubscriptionIdNotFoundException, InvalidTokenException {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		List<RefillOrder> actual = (refillOrderServiceImpl.getStatus(1, "token"));
		assertEquals(list, actual);
	}

	@Test
	void getStatusByInvalidToken() throws SubscriptionIdNotFoundException, InvalidTokenException {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", false);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);

		assertThrows(InvalidTokenException.class, () -> refillOrderServiceImpl.getStatus(1, "token"));

	}

	/*
	 * @Test void getStatusByInvalidSubId() throws SubscriptionIdNotFoundException,
	 * InvalidTokenException {
	 * 
	 * ArrayList<RefillOrder> list = new ArrayList<>(); RefillOrder refillOrder =
	 * new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
	 * list.add(refillOrder); List<RefillOrder> finallist = null; ValidateToken
	 * validateToken = new ValidateToken("azeem", "azeem", true);
	 * ResponseEntity<ValidateToken> response = new
	 * ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
	 * when(authFeign.getValidity("token")).thenReturn(response);
	 * when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>)
	 * list);
	 * 
	 * when(refillOrderServiceImpl.getStatus((long) 4,
	 * "token")).thenReturn(finallist);
	 * assertThrows(SubscriptionIdNotFoundException.class, () -> {
	 * list.stream().filter(p -> p.getSubId() == 1).collect(Collectors.toList());
	 * });
	 * 
	 * // List<RefillOrder> actual = (); // assertEquals(list, actual);
	 * 
	 * }
	 */

	@Test
	void getStatusByInvalidSubId() throws SubscriptionIdNotFoundException, InvalidTokenException {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);

		when(refillOrderServiceImpl.getStatus( 4L, "token")).thenReturn(null);
		assertThrows(SubscriptionIdNotFoundException.class,
				() -> refillOrderServiceImpl.getStatus(4L, "token"));
	
	}
	@Test
	void getStatusByMember() throws SubscriptionIdNotFoundException, InvalidTokenException, RefillEmptyException {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);

		List<RefillOrder> actual = (refillOrderServiceImpl.getStatusByMember("azeem", "token"));

		assertEquals(list, actual);

	}

	@Test
	void getStatusByInvalidMemberId()
			throws SubscriptionIdNotFoundException, InvalidTokenException, RefillEmptyException {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		assertThrows(RefillEmptyException.class, () -> refillOrderServiceImpl.getStatusByMember("gautam", "token"));

	}

	@Test
	void getStatusByMemberIdWithInvalidToken()
			throws SubscriptionIdNotFoundException, InvalidTokenException, RefillEmptyException {

		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", false);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		assertThrows(InvalidTokenException.class, () -> refillOrderServiceImpl.getStatusByMember("azeem", "token"));

	}

	@Test
	void getRefillPaymentDuesSuccess() throws InvalidTokenException {
		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), false, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		boolean actual = (refillOrderServiceImpl.getRefillPaymentDues(1, "token"));
		assertEquals(true, actual);
	}

	@Test
	void getRefillPaymentDuesSuccessWithInvalidToken() throws InvalidTokenException {
		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), false, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", false);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		assertThrows(InvalidTokenException.class, () -> refillOrderServiceImpl.getRefillPaymentDues(1, "token"));

//		boolean actual = (refillOrderServiceImpl.getRefillPaymentDues(1, "token"));
//		assertEquals(true, actual);
	}

	@Test
	void getRefillPaymentDuesUnsucess() throws InvalidTokenException {
		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), true, 1, 3, "azeem");
		list.add(refillOrder);
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		boolean actual = (refillOrderServiceImpl.getRefillPaymentDues(1, "token"));
		assertEquals(false, actual);
	}

	@Test
	void getRefillDuesAsOfDate() throws InvalidTokenException, SubscriptionListEmptyException {
		ArrayList<MemberSubscription> memberList = new ArrayList<>();
		memberList.add(new MemberSubscription(1, 1, "azeem", LocalDate.now(), 10, "Paracetamol", 3, "Chennai", "true"));
		memberList.add(new MemberSubscription(2, 2, "azeem", LocalDate.now(), 20, "Crocin", 3, "Chennai", "true"));
		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), false, 1, 3, "azeem");
		RefillOrder refillOrder2 = new RefillOrder(1, LocalDate.now(), false, 2, 3, "azeem");
		list.add(refillOrder);
		list.add(refillOrder2);

		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(subscriptionClient.getAllSubscription("token", "azeem")).thenReturn(memberList);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		when(refillOrderRepository.findBySubscriptionId(1L)).thenReturn(list);
		List<RefillOrder> refillDuesAsOfDate = refillOrderServiceImpl.getRefillDuesAsOfDate("azeem", "2021-03-12",
				"token");
		assertEquals(list, refillDuesAsOfDate);
	}

	@Test
	void getRefillDuesAsOfDateOfwithoutAnyRefillRequest() throws InvalidTokenException, SubscriptionListEmptyException {
		ArrayList<MemberSubscription> memberList = new ArrayList<>();
		memberList.add(new MemberSubscription(1, 1, "azeem", LocalDate.now(), 10, "Paracetamol", 3, "Chennai", "true"));
		memberList.add(new MemberSubscription(2, 2, "azeem", LocalDate.now(), 20, "Crocin", 3, "Chennai", "true"));
		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), false, 1, 3, "azeem");
		RefillOrder refillOrder2 = new RefillOrder(1, LocalDate.now(), false, 2, 3, "azeem");
		list.add(refillOrder);
		list.add(refillOrder2);

		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(subscriptionClient.getAllSubscription("token", "azeem")).thenReturn(memberList);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		when(refillOrderRepository.findBySubscriptionId(1L)).thenReturn(list);

		assertThrows(RefillEmptyException.class,
				() -> refillOrderServiceImpl.getRefillDuesAsOfDate("gautam", "2021-03-12", "token"));
	
	}

	@Test
	void getRefillDuesAsOfDateWithNoDues() throws InvalidTokenException, SubscriptionListEmptyException {
		ArrayList<MemberSubscription> memberList = new ArrayList<>();
		memberList.add(new MemberSubscription(1, 1, "azeem", LocalDate.now(), 10, "Paracetamol", 3, "Chennai", "true"));
		memberList.add(new MemberSubscription(2, 2, "azeem", LocalDate.now(), 20, "Crocin", 3, "Chennai", "true"));
		ArrayList<RefillOrder> list = new ArrayList<>();
		RefillOrder refillOrder = new RefillOrder(1, LocalDate.now(), false, 1, 3, "azeem");
		RefillOrder refillOrder2 = new RefillOrder(1, LocalDate.now(), false, 2, 3, "azeem");
		list.add(refillOrder);
		list.add(refillOrder2);

		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(subscriptionClient.getAllSubscription("token", "azeem")).thenReturn(memberList);
		when(refillOrderRepository.findAll()).thenReturn((ArrayList<RefillOrder>) list);
		when(refillOrderRepository.findBySubscriptionId(1L)).thenReturn(list);
		List<RefillOrder> refillDuesAsOfDate = refillOrderServiceImpl.getRefillDuesAsOfDate("azeem", "2021-03-16",
				"token");
		assertNotEquals(list, refillDuesAsOfDate);
	}

	
	 @Test 
	 void requestAdhocRefill() throws SubscriptionIdNotFoundException,
	  InvalidTokenException, FeignException, ParseException,
	  DrugQuantityNotAvailable, Exception { RefillOrder refillOrder = new RefillOrder((long)1, LocalDate.now(), true, 1, 3, "azeem");
	  
	  ResponseEntity<String> entityname = new ResponseEntity<String>("Paracetamol", HttpStatus.OK); 
		ValidateToken validateToken = new ValidateToken("azeem", "azeem", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(validateToken, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
	  
	  when(subscriptionClient.getDrugBySubscription((long)1, "token")).thenReturn(entityname); 
	  ResponseEntity<Object> responseValue = new  ResponseEntity("{responseMessage=Refill Done Successfully}",HttpStatus.OK); 
	  System.out.println("@@@@@@@@0"+responseValue);
	  when(drugDetailClient.updateQuantity("token", entityname.getBody(), "Chennai", 3)).thenReturn((ResponseEntity<Object>) responseValue); 
	  when(refillOrderServiceImpl.requestAdhocRefill((long)1, true, 3, "Chennai", "token", "azeem")).thenReturn(refillOrder);
	   when(refillOrderRepository.save(refillOrder)).thenReturn(refillOrder);
	   RefillOrder requestAdhocRefill = refillOrderServiceImpl.requestAdhocRefill((long)1, true, 3, "Chennai", "token", "azeem");
	 
	  
	  assertEquals(refillOrder.getSubId()+refillOrder.getMemberId()+refillOrder.getQuantity()
	  , requestAdhocRefill.getSubId()+requestAdhocRefill.getMemberId()+requestAdhocRefill.getQuantity()); }
	 
	 
	

}
