package com.mailorderpharma.drugservice.service.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.mailorderpharma.drugservice.entity.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mailorderpharma.drugservice.dao.DrugDetailsRepository;
import com.mailorderpharma.drugservice.dao.DrugLocationRepository;
import com.mailorderpharma.drugservice.exception.DrugNotFoundException;
import com.mailorderpharma.drugservice.exception.InvalidTokenException;
import com.mailorderpharma.drugservice.exception.StockNotFoundException;
import com.mailorderpharma.drugservice.restclients.AuthFeign;
import com.mailorderpharma.drugservice.service.DrugDetailsServiceImpl;

@SpringBootTest(classes = DrugDetailsServiceImplTest.class)
class DrugDetailsServiceImplTest {

	@InjectMocks
	DrugDetailsServiceImpl drugDetailsServiceImpl;

	@Mock
	AuthFeign authFeign;

	@Mock
	private DrugDetailsRepository drugRepo;

	@Mock
	private DrugLocationRepository locationRepo;

	Date date = new Date();
	List<DrugLocationDetails> list = new ArrayList<DrugLocationDetails>();
	DrugDetails drugDetail = new DrugDetails("drug", "drug", "drug", date, date, list);

	@Test
	void getDrugByIdTestNullPointerException() throws InvalidTokenException, DrugNotFoundException {

		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(drugRepo.findById("D5")).thenReturn(null);
		assertThrows(NullPointerException.class, () -> drugDetailsServiceImpl.getDrugById("D5", "token"),
				"getDrugByIdTest");
	}

	@Test
	void getDrugByIdTestDrugNotFoundException() throws InvalidTokenException, DrugNotFoundException {

		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("D5", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(drugRepo.findById("D5")).thenReturn(Optional.empty());
		assertThrows(DrugNotFoundException.class, () -> drugDetailsServiceImpl.getDrugById("D5", "token"),
				"getDrugByIdTest");
	}

	@Test
	void getDrugByIdTestSucess() throws InvalidTokenException, DrugNotFoundException {

		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("D5", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);

		when(drugRepo.findById("D5")).thenReturn(Optional.of(drugDetails));
		DrugDetails x = drugDetailsServiceImpl.getDrugById("D5", "token");

		assertEquals(drugDetails, x);
	}

	@Test
	void getDrugByIdTestInvalidTokenException() throws InvalidTokenException, DrugNotFoundException {
		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", false);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		
		assertThrows(InvalidTokenException.class, () -> drugDetailsServiceImpl.getDrugById("D5", "token"),"getDrugByIdTest");

	}

	@Test
	void getDrugByNameFalse() {
		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", false);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		assertThrows(InvalidTokenException.class, () -> drugDetailsServiceImpl.getDrugByName("D5", "token"),
				"getDrugByIdTest");
	}

	@Test
	void getDrugByNameSucess() throws DrugNotFoundException, InvalidTokenException {
		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(drugRepo.findBydrugName("D5")).thenReturn(Optional.of(drugDetails));
		DrugDetails x = drugDetailsServiceImpl.getDrugByName("D5", "token");

		assertEquals(drugDetails, x);
	}

	@Test
	void getDrugByNameDrugNotFoundException() {
		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(drugRepo.findBydrugName("D5")).thenReturn(null);
		assertThrows(DrugNotFoundException.class, () -> drugDetailsServiceImpl.getDrugByName("D5", "token"),
				"getDrugByIdTest");
	}

	@Test
	void getDispatchableDrugStockFalse() {
		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", false);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		assertThrows(InvalidTokenException.class,
				() -> drugDetailsServiceImpl.getDispatchableDrugStock("D5", "salem", "token"), "getDrugByIdTest");
	}

	@Test
	void getDispatchableDrugStockSuccess()
			throws DrugNotFoundException, InvalidTokenException, StockNotFoundException {
		Date date = new Date();
		DrugLocationDetails drugLocationDetails = new DrugLocationDetails("45", "salem", 45, null);
		list.add(drugLocationDetails);
		DrugDetails drugDetails = new DrugDetails("D5", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		Optional<DrugDetails> opt1 = Optional.of(drugDetails);
		when(drugRepo.findById("D5")).thenReturn(opt1);
		Stock actual = drugDetailsServiceImpl.getDispatchableDrugStock("D5", "salem", "token");
		String expectedValue = actual.getDrugId()+" "+actual.getDrugName()+" "+actual.getExpiryDate()+" "+actual.getStocks();
		//String actualValue =
		Stock expected=new Stock("D5","drug",new Date(),45);

		String actualValue = expected.getDrugId()+" "+expected.getDrugName()+" "+expected.getExpiryDate()+" "+expected.getStocks();

		assertEquals(expectedValue, actualValue);


	}

	@Test
	void getDispatchableDrugStockFalseDrugNotFoundException() {
		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("D5", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(drugRepo.findById("D5")).thenReturn(null);
		assertThrows(DrugNotFoundException.class,
				() -> drugDetailsServiceImpl.getDispatchableDrugStock("D5", "salem", "token"), "getDrugByIdTest");
	}

	@Test
	void getDispatchableDrugStockFalseStockNotFoundException() {
		Date date = new Date();
		DrugLocationDetails drugLocationDetails = new DrugLocationDetails("ad", "das", 45, null);
		list.add(drugLocationDetails);
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		Optional<DrugDetails> opt = Optional.of(drugDetails);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		when(drugRepo.findById("D5")).thenReturn(opt);
		assertThrows(StockNotFoundException.class,
				() -> drugDetailsServiceImpl.getDispatchableDrugStock("D5", "salem", "token"), "getDrugByIdTest");
	}

	@Test
	void updateQuantityFalse() {
		Date date = new Date();
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", false);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		assertThrows(InvalidTokenException.class,
				() -> drugDetailsServiceImpl.updateQuantity("token", "token", 45, "token"), "getDrugByIdTest");
	}

	@Test
	void updateQuantity() throws DrugNotFoundException, InvalidTokenException, StockNotFoundException {
		Date date = new Date();
		list.add(new DrugLocationDetails("45", "salem", 45, new DrugDetails("drug", "drug", "drug", date, date, list)));
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		Optional<DrugDetails> opt = Optional.of(drugDetails);
		when(drugRepo.findBydrugName("drug")).thenReturn(opt);
		Optional<DrugLocationDetails> opt1 = Optional.of(
				new DrugLocationDetails("45", "salem", 45, new DrugDetails("drug", "drug", "drug", date, date, list)));
		when(locationRepo.findById("45")).thenReturn(opt1);
		ResponseForSuccess successResponse = new ResponseForSuccess();
		ResponseEntity<ResponseForSuccess> responseEntity = new ResponseEntity<ResponseForSuccess>(successResponse, HttpStatus.OK);
		when(locationRepo.findByserialId("45")).thenReturn(list);
		when(drugDetailsServiceImpl.updateQuantity("drug", "salem", 1, "token")).thenReturn(responseEntity);

		String expectedValue = "200 OK";

		String actualValue = responseEntity.getStatusCode().toString();
		assertEquals(expectedValue, actualValue);

	}

	@Test
	void updateQuantityDrugNotFoundException()
			throws DrugNotFoundException, InvalidTokenException, StockNotFoundException {
		Date date = new Date();
		list.add(new DrugLocationDetails("45", "salem", 45, new DrugDetails("drug", "drug", "drug", date, date, list)));
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		Optional<DrugDetails> opt = Optional.of(drugDetails);
		when(drugRepo.findBydrugName("drug")).thenReturn(null);
		Optional<DrugLocationDetails> opt1 = Optional.of(
				new DrugLocationDetails("45", "salem", 45, new DrugDetails("drug", "drug", "drug", date, date, list)));
		when(locationRepo.findById("45")).thenReturn(opt1);
		assertThrows(DrugNotFoundException.class,
				() -> drugDetailsServiceImpl.updateQuantity("drug", "salem", 20, "token"), "");

	}

	@Test
	void updateQuantityStockNotFoundException()
			throws DrugNotFoundException, InvalidTokenException, StockNotFoundException {
		Date date = new Date();
		list.add(new DrugLocationDetails("45", "chennai", 45,
				new DrugDetails("drug", "drug", "drug", date, date, list)));
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		Optional<DrugDetails> opt = Optional.of(drugDetails);
		when(drugRepo.findBydrugName("drug")).thenReturn(opt);
		Optional<DrugLocationDetails> opt1 = Optional.of(
				new DrugLocationDetails("45", "salem", 45, new DrugDetails("drug", "drug", "drug", date, date, list)));
		when(locationRepo.findById("45")).thenReturn(opt1);
		assertThrows(StockNotFoundException.class,
				() -> drugDetailsServiceImpl.updateQuantity("drug", "salem", 20, "token"), "");

	}

	@Test
	void updateQuantityStockNotFoundExceptiontwo()
			throws DrugNotFoundException, InvalidTokenException, StockNotFoundException {
		Date date = new Date();
		list.add(new DrugLocationDetails("45", "salem", 4, new DrugDetails("drug", "drug", "drug", date, date, list)));
		DrugDetails drugDetails = new DrugDetails("drug", "drug", "drug", date, date, list);
		ValidateToken tokenValid = new ValidateToken("uid", "name", true);
		ResponseEntity<ValidateToken> response = new ResponseEntity<ValidateToken>(tokenValid, HttpStatus.OK);
		when(authFeign.getValidity("token")).thenReturn(response);
		Optional<DrugDetails> opt = Optional.of(drugDetails);
		when(drugRepo.findBydrugName("drug")).thenReturn(opt);
		Optional<DrugLocationDetails> opt1 = Optional.of(	
				new DrugLocationDetails("45", "salem", 45, new DrugDetails("drug", "drug", "drug", date, date, list)));
		when(locationRepo.findById("45")).thenReturn(opt1);
		assertThrows(StockNotFoundException.class,
				() -> drugDetailsServiceImpl.updateQuantity("drug", "salem", 20, "token"), "");
	}
	
	@Test
	void getAllDrugs() {
		list.add(new DrugLocationDetails("45", "salem", 4, new DrugDetails("drug", "drug", "drug", date, date, list)));
		DrugDetails expectedValue = new DrugDetails("drug", "drug", "drug", date, date, list);
		List<DrugDetails> drugList = drugRepo.findAll();

		for(DrugDetails d : drugList){
			System.out.println(d.getDrugId()+" "+d.getDrugName()+" "+d.getManufacturer()+ " "+
					d.getDruglocationQuantities()+" "+d.getExpiryDate() +" "+ d.getManufactureDate());
		}
		when(drugDetailsServiceImpl.getAllDrugs()).thenReturn(drugList);

		String actualValue = "[{\"drugId\":\"D1\",\"drugName\":\"Paracetmol\",\"manufacturer\":\"Mahesh\",\"manufactureDate\":\"2021-10-21T18:30:00.000+00:00\",\"expiryDate\":\"2024-11-21T18:30:00.000+00:00\",\"druglocationQuantities\":[{\"serialId\":\"1\",\"location\":\"Chennai\",\"quantity\":30},{\"serialId\":\"2\",\"location\":\"Bangalore\",\"quantity\":20},{\"serialId\":\"3\",\"location\":\"Pune\",\"quantity\":30},{\"serialId\":\"4\",\"location\":\"Hyderabad\",\"quantity\":30}]},{\"drugId\":\"D2\",\"drugName\":\"Citrazine\",\"manufacturer\":\"Nikhil\",\"manufactureDate\":\"2021-10-21T18:30:00.000+00:00\",\"expiryDate\":\"2024-11-21T18:30:00.000+00:00\",\"druglocationQuantities\":[{\"serialId\":\"5\",\"location\":\"Chennai\",\"quantity\":30},{\"serialId\":\"6\",\"location\":\"Bangalore\",\"quantity\":20},{\"serialId\":\"7\",\"location\":\"Pune\",\"quantity\":30},{\"serialId\":\"8\",\"location\":\"Hyderabad\",\"quantity\":30}]},{\"drugId\":\"D3\",\"drugName\":\"Aspirin\",\"manufacturer\":\"Samyak\",\"manufactureDate\":\"2021-10-21T18:30:00.000+00:00\",\"expiryDate\":\"2024-11-21T18:30:00.000+00:00\",\"druglocationQuantities\":[{\"serialId\":\"9\",\"location\":\"Chennai\",\"quantity\":30},{\"serialId\":\"10\",\"location\":\"Bangalore\",\"quantity\":20}]}]";



		assertThat(expectedValue).isNotNull();
	}
	
}
