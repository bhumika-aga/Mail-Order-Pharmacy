package com.mailorderpharma.drugservice.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.mailorderpharma.drugservice.entity.DrugDetails;
import com.mailorderpharma.drugservice.entity.ResponseForSuccess;
import com.mailorderpharma.drugservice.entity.Stock;
import com.mailorderpharma.drugservice.exception.InvalidTokenException;
import com.mailorderpharma.drugservice.exception.StockNotFoundException;


public interface DrugDetailsService {

	/**
	 * 
	 * @param id
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	DrugDetails getDrugById(String id, String token) throws InvalidTokenException;

	/**
	 * 
	 * @param name
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	DrugDetails getDrugByName(String name, String token) throws InvalidTokenException;

	/**
	 * 
	 * @param id
	 * @param location
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 * @throws StockNotFoundException
	 */
	Stock getDispatchableDrugStock(String id, String location, String token)
			throws InvalidTokenException, StockNotFoundException;
	
	
	/**
	 * 
	 * @param id
	 * @param location
	 * @param quantity
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 * @throws StockNotFoundException
	 */
	ResponseEntity<ResponseForSuccess> updateQuantity(String id, String location, int quantity, String token)
			throws InvalidTokenException,StockNotFoundException;

	/**
	 * 
	 * @return
	 */
	List<DrugDetails> getAllDrugs();

}
