package com.mailorderpharma.webportal.restclients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.mailorderpharma.webportal.entity.DrugDetails;

@FeignClient(url = "${drugservice.client.url}", name = "${drugservice.client.name}")
public interface DrugClient {

	@GetMapping("/getAllDrugs")
	public List<DrugDetails> getAllDrugs(); 
	
	@GetMapping("/searchDrugsById/{id}")
	public DrugDetails getDrugById(@RequestHeader("Authorization") String token, @PathVariable("id") String id);
	
	@GetMapping("/searchDrugsByName/{name}")
	public DrugDetails getDrugByName(@RequestHeader("Authorization") String token, @PathVariable("name") String name);
}
