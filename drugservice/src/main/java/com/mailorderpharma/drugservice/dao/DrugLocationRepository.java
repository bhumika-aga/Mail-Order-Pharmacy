package com.mailorderpharma.drugservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mailorderpharma.drugservice.entity.DrugLocationDetails;

@Repository
public interface DrugLocationRepository extends JpaRepository<DrugLocationDetails, String>{

	/**
	 * 
	 * @param string
	 * @return
	 */
	List<DrugLocationDetails> findByserialId(String string);

}
