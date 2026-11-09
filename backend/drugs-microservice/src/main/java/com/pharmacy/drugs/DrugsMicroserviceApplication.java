package com.pharmacy.drugs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // prunes old stock release records (StockReleaseRecordPruner)
public class DrugsMicroserviceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DrugsMicroserviceApplication.class, args);
    }
}