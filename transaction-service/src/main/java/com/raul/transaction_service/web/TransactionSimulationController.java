package com.raul.transaction_service.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raul.transaction_service.generator.FraudScenario;
import com.raul.transaction_service.generator.TransactionGeneratorService;
import com.raul.transaction_service.generator.TransactionSimulationResult;

@RestController
@RequestMapping("/api/transactions")
public class TransactionSimulationController {

	private final TransactionGeneratorService transactionGeneratorService;

	public TransactionSimulationController(TransactionGeneratorService transactionGeneratorService) {
		this.transactionGeneratorService = transactionGeneratorService;
	}

	@PostMapping("/generate")
	public ResponseEntity<TransactionSimulationResult> generateRandom() {
		return ResponseEntity.ok(transactionGeneratorService.generateAndSubmitRandomTransaction());
	}

	@PostMapping("/generate/legitimate")
	public ResponseEntity<TransactionSimulationResult> generateLegitimate() {
		return ResponseEntity.ok(transactionGeneratorService.generateAndSubmitLegitimateTransaction());
	}

	@PostMapping("/generate/fraud/{scenario}")
	public ResponseEntity<TransactionSimulationResult> generateFraud(@PathVariable FraudScenario scenario) {
		return ResponseEntity.ok(transactionGeneratorService.generateAndSubmitFraudScenario(scenario));
	}

	@GetMapping("/profiles")
	public ResponseEntity<?> profiles() {
		return ResponseEntity.ok(transactionGeneratorService.customerProfiles());
	}

	//Todo maybe move this out of here or make it something that calls the database instead of hardcoded
	@GetMapping("/fraud-accounts")
	public ResponseEntity<?> fraudAccounts() {
		return ResponseEntity.ok(transactionGeneratorService.knownFraudAccounts());
	}
}
