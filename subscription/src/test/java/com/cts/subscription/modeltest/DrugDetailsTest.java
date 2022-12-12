package com.cts.subscription.modeltest;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cts.subscription.model.DrugDetails;
import com.cts.subscription.model.DrugLocationDetails;


class DrugDetailsTest {

	DrugDetails drugDetails = new DrugDetails();
	Date date = new Date();
	List<DrugLocationDetails> list = new ArrayList<DrugLocationDetails>();
	DrugDetails drugDetails2 = new DrugDetails("D1","Drug1","Manufacturer1",date,date,new ArrayList<>());
	
	@Test
	void testDrugId() {
		drugDetails.setDrugId("D1");
		assertEquals("D1", drugDetails.getDrugId());
	}

	@Test
	void testDrugName() {
		drugDetails.setDrugName("Drug1");
		assertEquals("Drug1", drugDetails.getDrugName());
	}

	@Test
	void testDrugManufacturer() {
		drugDetails.setManufacturer("Manufacturer1");;
		assertEquals("Manufacturer1", drugDetails.getManufacturer());
	}

	@Test
	void testDrugManufactureDate() {
		drugDetails.setManufactureDate(date);
		assertEquals(date, drugDetails.getManufactureDate());
	}
	
	@Test
	void testDrugExpireDate() {
		drugDetails.setExpiryDate(date);
		assertEquals(date, drugDetails2.getExpiryDate());
	}
	
	@Test
	void testDrugLocationQuantities() {
		drugDetails.setDruglocationQuantities(list);
		assertEquals(list,drugDetails.getDruglocationQuantities());
	}

}
