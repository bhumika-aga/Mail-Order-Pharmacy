package com.cts.subscription.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.cts.subscription.exception.DrugNotFoundException;
import com.cts.subscription.exception.InvalidTokenException;
import com.cts.subscription.model.DrugDetails;



@FeignClient(name = "${drugs.client.name}",url = "${drugs.client.url}")
public interface DrugClient {

	/**
	 * 
	 * @param token
	 * @param name
	 * @return
	 * @throws InvalidTokenException
	 * @throws DrugNotFoundException
	 */
	@GetMapping("/searchDrugsByName/{name}")
	public DrugDetails getDrugByName(@RequestHeader("Authorization") String token, @PathVariable("name") String name)
			throws InvalidTokenException,DrugNotFoundException;
	/**
	 * 		
	 * @param token
	 * @param name
	 * @param location
	 * @param quantity
	 * @return
	 */
	@PutMapping("/updateDispatchableDrugStock/{name}/{location}/{quantity}")
	public ResponseEntity<Object> updateQuantity(@RequestHeader("Authorization") String token,@PathVariable("name") String name, @PathVariable("location") String location,
			@PathVariable("quantity") int quantity);


}
