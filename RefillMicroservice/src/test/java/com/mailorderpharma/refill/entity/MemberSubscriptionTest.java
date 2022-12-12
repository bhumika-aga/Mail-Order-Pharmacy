package com.mailorderpharma.refill.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberSubscriptionTest {

	MemberSubscription member = new MemberSubscription(1, 1, "ayush", LocalDate.now(), 20, "Paracetamol", 3, "Chennai",
			"true");
	MemberSubscription member2 = new MemberSubscription();

	@Test
	public void testSubscriptionId() {
		assertEquals(1, member.getSubscriptionId());
	}

	@Test
	public void testPrescriptionId() {
		assertEquals(1, member.getPrescriptionId());
	}

	@Test
	public void testPrescriptionDate() {
		assertEquals(LocalDate.now(), member.getDate());
	}

	@Test
	public void testDrugName() {
		assertEquals("Paracetamol", member.getDrugName());

	}

	@Test
	public void testDrugQuantity() {
		assertEquals(20, member.getQuantity());

	}

	@Test
	public void testDrugLocation() {
		assertEquals("Chennai", member.getMemberLocation());

	}

	@Test
	public void testRefillOccurence() {
		assertEquals(3, member.getRefillOccurrence());

	}

	@Test
	public void testPaymentStatus() {
		assertEquals("true", member.getStatus());

	}

	@Test
	public void testSubscriptionId1() {
		member2.setSubscriptionId(1);
		assertEquals(1, member.getSubscriptionId());
	}

	@Test
	public void testPrescriptionId2() {
		member2.setPrescriptionId(1);
		assertEquals(1, member.getPrescriptionId());
	}

	@Test
	public void testPrescriptionDate2() {
		member2.setDate(LocalDate.now());
		assertEquals(LocalDate.now(), member.getDate());
	}

	@Test
	public void testDrugName2() {
		member2.setDrugName("Paracetamol");
		assertEquals("Paracetamol", member.getDrugName());

	}

	@Test
	public void testDrugQuantity2() {
		member2.setQuantity(20);
		assertEquals(20, member.getQuantity());

	}

	@Test
	public void testDrugLocation2() {
		member2.setMemberLocation("Chennai");
		assertEquals("Chennai", member.getMemberLocation());

	}

	@Test
	public void testRefillOccurence2() {
		member2.setRefillOccurrence(3);
		assertEquals(3, member.getRefillOccurrence());

	}

	@Test
	public void testPaymentStatus2() {
		member2.setStatus("true");
		assertEquals("true", member.getStatus());

	}
	@Test
	public void testMemberId() {
		
		assertEquals("ayush", member.getMemberId());

	}
	@Test
	public void testMemberId1() {
		member2.setMemberId("Gautam");
		assertEquals("Gautam", member2.getMemberId());

	}
	
	

}
