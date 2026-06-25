package com.raul.fraud_case_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FraudCaseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudCaseServiceApplication.class, args);
	}

}
