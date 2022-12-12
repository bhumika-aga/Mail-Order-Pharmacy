package com.mailorderpharma.refill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**Main class which contains the main function which 
 * triggers the whole application 
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableFeignClients("com")
public class RefillMicroserviceApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(RefillMicroserviceApplication.class, args);
	}
}
