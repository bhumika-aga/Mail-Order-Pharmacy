package com.mailorderpharma.drugservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mailorderpharma.drugservice.entity.DrugDetails;
import com.mailorderpharma.drugservice.entity.ResponseForSuccess;
import com.mailorderpharma.drugservice.entity.Stock;
import com.mailorderpharma.drugservice.exception.DrugNotFoundException;
import com.mailorderpharma.drugservice.exception.InvalidTokenException;
import com.mailorderpharma.drugservice.exception.StockNotFoundException;
import com.mailorderpharma.drugservice.service.DrugDetailsService;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SAI PRIYA
 * @see
 */

@Slf4j
@RestController
@RequestMapping("/api")
public class DrugController {

	@Autowired
	DrugDetailsService drugDetailsService;

	private static final Logger log = LoggerFactory.getLogger(DrugController.class);

	String msg = "Drug Not Found";

	/**
	 * 
	 * @return
	 */
	@ApiOperation(value = "Get all drugs", response = List.class)
	@GetMapping("/getAllDrugs")
	public List<DrugDetails> getAllDrugs() {
		log.info("start--Controller--getAllDrugs");
		return drugDetailsService.getAllDrugs();
	}

	/**
	 * 
	 * @param token
	 * @param id
	 * @return
	 * @throws InvalidTokenException
	 */
	@ApiOperation(value = "Search drug by id", response = DrugDetails.class)
	@GetMapping("/searchDrugsById/{id}")
	public DrugDetails getDrugById(@RequestHeader("Authorization") String token, @PathVariable("id") String id)
			throws InvalidTokenException, DrugNotFoundException {
		try {
			log.info("start--Controller--getDrugById");
			return drugDetailsService.getDrugById(id, token);
		} catch (DrugNotFoundException d) {
			log.info("Catch--Controller--getDrugById");
			throw new DrugNotFoundException(msg);
		}
	}

	/**
	 * 
	 * @param token
	 * @param name
	 * @return
	 * @throws InvalidTokenException
	 */
	@ApiOperation(value = "Search drug by name", response = DrugDetails.class)
	@GetMapping("/searchDrugsByName/{name}")
	public DrugDetails getDrugByName(@RequestHeader("Authorization") String token, @PathVariable("name") String name)
			throws InvalidTokenException {
		try {
			log.info("start--Controller--getDrugByName");
			return drugDetailsService.getDrugByName(name, token);
		} catch (DrugNotFoundException d) {
			log.info("Catch--Controller--getDrugByName");
			throw new DrugNotFoundException(msg);
		}
	}

	/**
	 * 
	 * @param token
	 * @param id
	 * @param location
	 * @return
	 * @throws InvalidTokenException
	 * @throws StockNotFoundException
	 */
	@ApiOperation(value = "Search stock by id and Location", response = Stock.class)
	@PostMapping("/getDispatchableDrugStock/{id}/{location}")
	public Stock getDispatchableDrugStock(@RequestHeader("Authorization") String token, @PathVariable("id") String id,
			@PathVariable("location") String location) throws InvalidTokenException, StockNotFoundException {
		try {
			log.info("start--Controller--getDispatchableDrugStock");
			return drugDetailsService.getDispatchableDrugStock(id, location, token);
		} catch (DrugNotFoundException d) {
			log.info("Catch--Controller--getDispatchableDrugStock");
			throw new DrugNotFoundException(msg);
		}
	}

	/**
	 * 
	 * @param token
	 * @param name
	 * @param location
	 * @param quantity
	 * @return
	 * @throws InvalidTokenException
	 * @throws StockNotFoundException
	 */
	@ApiOperation(value = "Update quantity by stock", response = ResponseEntity.class)
	@PutMapping("/updateDispatchableDrugStock/{name}/{location}/{quantity}")
	public ResponseEntity<ResponseForSuccess> updateQuantity(@RequestHeader("Authorization") String token,
			@PathVariable("name") String name, @PathVariable("location") String location,
			@PathVariable("quantity") int quantity) throws InvalidTokenException, StockNotFoundException {
		try {
			log.info("start--Controller--updateQuantity");
			return drugDetailsService.updateQuantity(name, location, quantity, token);
		} catch (DrugNotFoundException d) {
			log.info("Catch--Controller--updateQuantity");
			throw new DrugNotFoundException(msg);
		}
	}
}