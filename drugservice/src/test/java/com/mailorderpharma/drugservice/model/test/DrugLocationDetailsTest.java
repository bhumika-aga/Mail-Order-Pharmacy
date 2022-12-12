package com.mailorderpharma.drugservice.model.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mailorderpharma.drugservice.entity.DrugDetails;
import com.mailorderpharma.drugservice.entity.DrugLocationDetails;

class DrugLocationDetailsTest {

	DrugDetails drugDetails = new DrugDetails();
	DrugLocationDetails details1 = new DrugLocationDetails();
	DrugLocationDetails details2 = new DrugLocationDetails("ABC","Chennai",25,drugDetails);

	@Test
	void testSerialId() {
		details1.setSerialId("ABC");
		assertEquals("ABC", details2.getSerialId());
	}
	
	@Test
	void testLocation() {
		details1.setLocation("Chennai");
		assertEquals("Chennai",details2.getLocation());
	}
	
	@Test
	void testQuantity() {
		details1.setQuantity(25);
		assertEquals(25,details2.getQuantity());
	}
	
	@Test
	void testDrugList() {
		details1.setDrugDetails(drugDetails);
		assertEquals(drugDetails,details2.getDrugDetails());
	}
}
