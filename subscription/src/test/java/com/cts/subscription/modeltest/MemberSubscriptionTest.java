package com.cts.subscription.modeltest;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;



import org.junit.jupiter.api.Test;

import com.cts.subscription.model.MemberSubscription;





 class MemberSubscriptionTest {

	MemberSubscription memberSubscription = new MemberSubscription();

	@Test
	public void test() {
		
		memberSubscription.setSubscriptionId(1L);
		memberSubscription.setDrugName("Paracetamol");
		memberSubscription.setMemberId("Harshit");
		memberSubscription.setMemberLocation("Chennai");
		memberSubscription.setPrescriptionId(1L);
		memberSubscription.setQuantity(2);
		memberSubscription.setRefillOccurrence(2);
		memberSubscription.setStatus("active");
		memberSubscription.setDate(LocalDate.now());
		
		assertEquals(1L,memberSubscription.getSubscriptionId());
		assertEquals("Paracetamol",memberSubscription.getDrugName());
		assertEquals("Harshit",memberSubscription.getMemberId());
		assertEquals("Chennai",memberSubscription.getMemberLocation());
		assertEquals(1L,memberSubscription.getPrescriptionId());
		assertEquals(2,memberSubscription.getQuantity());
		assertEquals(2,memberSubscription.getRefillOccurrence());
		assertEquals("active",memberSubscription.getStatus());
		assertEquals(LocalDate.now(), memberSubscription.getDate());
	}
}
