package com.mailorderpharma.drugservice.model.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mailorderpharma.drugservice.entity.Stock;

class StockTest {

	Date date = new Date();
	Stock stock1 = new Stock();
	Stock stock2 = new Stock("PR1","Paracetamol",date,500);
	
	@Test
	void testDrugId() {
		stock1.setDrugId("PR1");
		assertEquals("PR1", stock1.getDrugId());
	}
	
	@Test
	void testDrugName() {
		stock1.setDrugName("Paracetamol");
		assertEquals("Paracetamol",stock1.getDrugName());
	}
	
	@Test
	void testDate() {
		stock1.setExpiryDate(date);
		assertEquals(date, stock1.getExpiryDate());
	}
	
	@Test
	void testStock() {
		stock1.setStocks(500);
		assertEquals(500, stock1.getStocks());
	}
}
