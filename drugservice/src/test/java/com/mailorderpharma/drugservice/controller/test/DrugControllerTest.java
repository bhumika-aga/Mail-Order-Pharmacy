package com.mailorderpharma.drugservice.controller.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailorderpharma.drugservice.controller.DrugController;
import com.mailorderpharma.drugservice.dao.DrugDetailsRepository;
import com.mailorderpharma.drugservice.dao.DrugLocationRepository;
import com.mailorderpharma.drugservice.entity.DrugDetails;
import com.mailorderpharma.drugservice.entity.DrugLocationDetails;
import com.mailorderpharma.drugservice.entity.ResponseForSuccess;
import com.mailorderpharma.drugservice.entity.Stock;
import com.mailorderpharma.drugservice.entity.ValidateToken;
import com.mailorderpharma.drugservice.restclients.AuthFeign;
import com.mailorderpharma.drugservice.service.DrugDetailsService;

@AutoConfigureMockMvc
@SpringBootTest
class DrugControllerTest {

	@InjectMocks
	DrugController drugController;

	@Mock
	DrugDetailsService drugDetailsService;

	@Autowired
	DrugDetailsRepository drugDetailsRepository;

	@Autowired
	DrugLocationRepository drugLocationRepository;

	@MockBean
	AuthFeign authFeign;

	@Autowired
	MockMvc mockMvc;

	List<DrugLocationDetails> list = new ArrayList<DrugLocationDetails>();

	@Test
	void testGetDrugById() throws Exception {
		list.add(new DrugLocationDetails("PR1", "Chennai", 30, null));
		DrugDetails expected = new DrugDetails("PR1", "Drug1", "manu1", new Date(), new Date(), list);
		ObjectMapper objectMapper = new ObjectMapper();
		String expectedValue = objectMapper.writeValueAsString(expected).substring(11, 13);
		ValidateToken tokenValid = new ValidateToken("uid", "uname", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("Bearer Token")).thenReturn(response);
		when(drugDetailsService.getDrugById( "PR1", "Bearer Token")).thenReturn(expected);
		MvcResult result = mockMvc.perform(get("/api/searchDrugsById/PR1").header("Authorization", "Bearer Token")).andReturn();
		String actualValue = result.getResponse().getContentAsString().substring(11,13);
		
		
		assertEquals(expectedValue,actualValue);
	}

	@Test
	void testGetDrugByName() throws Exception {
		list.add(new DrugLocationDetails("PR1", "Chennai", 30, null));
		DrugDetails expected = new DrugDetails("PR1", "Paracetamol", "manu1", new Date(), new Date(), list);
		ObjectMapper objectMapper = new ObjectMapper();
		String expectedValue = objectMapper.writeValueAsString(expected).substring(27, 37);
		ValidateToken tokenValid = new ValidateToken("uid", "uname", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("Bearer Token")).thenReturn(response);
		MvcResult result = mockMvc.perform(get("/api/searchDrugsByName/Paracetamol").header("Authorization", "Bearer Token"))
				.andReturn();
		String actualValue = result.getResponse().getContentAsString().substring(27, 37);
		assertEquals(expectedValue, actualValue);
	}

	@Test
	void testDispatchableDrugStock() throws Exception {
		Stock expectedStock = new Stock("PR1","Paracetamol",new Date(),500);
		ValidateToken tokenValid = new ValidateToken("uid", "uname", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("Bearer Token")).thenReturn(response);
		when(drugDetailsService.getDispatchableDrugStock("PR1", "Chennai", "Bearer token")).thenReturn(expectedStock);
		when(drugController.getDispatchableDrugStock("Bearer token", "D1", "Chennai")).thenReturn(expectedStock);

		MvcResult result = mockMvc.perform(post("/api/getDispatchableDrugStock/PR1/Hyderabad").header("Authorization", "Bearer Token"))
				.andReturn();
		String actualValue = result.getResponse().getContentAsString();

		String expectedValue = "{\"drugId\":\"PR1\",\"drugName\":\"Paracetamol\","
				+ "\"expiryDate\":\"2024-11-21T18:30:00.000+00:00\",\"stocks\":500}";


		assertEquals(expectedValue, actualValue);


	}

	@Test
	void testupdateQuantity() throws Exception {
		ValidateToken tokenValid = new ValidateToken("uid", "uname", true);
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseEntity<ResponseForSuccess> expectedValue = new ResponseEntity<ResponseForSuccess>(
				new ResponseForSuccess("Refill done successfully"), HttpStatus.OK);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("Bearer Token")).thenReturn(response);
		when(drugDetailsService.updateQuantity("D1", "Chennai", 1, "Bearer token")).thenReturn(expectedValue);
		when(drugController.updateQuantity("D1", "Chennai", "Bearer token", 1)).thenReturn(expectedValue);

		MvcResult result = mockMvc
				.perform(get("/updateDispatchableDrugStock/D1/Hyderabad/1").header("Authorization", "Bearer Token"))
				.andReturn();

		HttpStatus actualValue = response.getStatusCode();

		HttpStatus exp = expectedValue.getStatusCode();

		assertEquals(exp, actualValue);
	}

	@Test
	void testgetAllDrugs() throws Exception {
		ValidateToken tokenValid = new ValidateToken("uid", "uname", true);
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		List<DrugDetails> expectedValue = new ArrayList<>();
		list.add(new DrugLocationDetails("D1", "Chennai", 30, null));
		DrugDetails expected = new DrugDetails("D1", "Drug1", "manu1", new Date(), new Date(), list);
		expectedValue.add(expected);
		when(authFeign.getValidity("Bearer Token")).thenReturn(response);
		when(drugDetailsService.getAllDrugs()).thenReturn(expectedValue);
		when(drugController.getAllDrugs()).thenReturn(expectedValue);

		MvcResult result = mockMvc.perform(get("/api/getAllDrugs").header("Authorization", "Bearer Token")).andReturn();

		String actualValue = result.getResponse().getContentAsString();

		String expectedResult = "[{\"drugId\":\"PR1\",\"drugName\":\"Paracetamol\",\"manufacturer\":\"Azeem\",\"manufactureDate\":"
				+ "\"2021-10-21T18:30:00.000+00:00\",\"expiryDate\":\"2024-11-21T18:30:00.000+00:00\",\"druglocationQuantities\""
				+ ":[{\"serialId\":\"1\",\"location\":\"Chennai\",\"quantity\":500},{\"serialId\":\"2\",\"location\":"
				+ "\"Bangalore\",\"quantity\":500},{\"serialId\":\"3\",\"location\":\"Pune\",\"quantity\":500},"
				+ "{\"serialId\":\"4\",\"location\":\"Hyderabad\",\"quantity\":500}]},"
				+ "{\"drugId\":\"CR2\",\"drugName\":\"Crocin\",\"manufacturer\":\"Ayush\","
				+ "\"manufactureDate\":\"2021-10-21T18:30:00.000+00:00\","
				+ "\"expiryDate\":\"2024-11-21T18:30:00.000+00:00\","
				+ "\"druglocationQuantities\":"
				+ "[{\"serialId\":\"5\",\"location\":\"Chennai\",\"quantity\":500},"
				+ "{\"serialId\":\"6\",\"location\":\"Bangalore\",\"quantity\":500},"
				+ "{\"serialId\":\"7\",\"location\":\"Pune\",\"quantity\":500},"
				+ "{\"serialId\":\"8\",\"location\":\"Hyderabad\",\"quantity\":500}]},"
				+ "{\"drugId\":\"PD3\",\"drugName\":\"Penadol\",\"manufacturer\":\"Gautam\","
				+ "\"manufactureDate\":\"2021-10-21T18:30:00.000+00:00\","
				+ "\"expiryDate\":\"2024-11-21T18:30:00.000+00:00\",\"druglocationQuantities\""
				+ ":[{\"serialId\":\"9\",\"location\":\"Chennai\",\"quantity\":500},"
				+ "{\"serialId\":\"10\",\"location\":\"Bangalore\",\"quantity\":500}]}]";

		assertEquals(expectedResult, actualValue);
	}
}
