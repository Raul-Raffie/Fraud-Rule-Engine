package com.raul.fraud_case_service.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raul.fraud_case_service.web.dto.FraudCaseIngestRequest;
import com.raul.fraud_case_service.web.dto.FraudCaseIngestResponse;
import com.raul.fraud_case_service.service.FraudTransactionService;

@RestController
@RequestMapping("/internal/fraud-cases")
public class FraudCaseIngestController {

	private final FraudTransactionService fraudTransactionService;

	public FraudCaseIngestController(FraudTransactionService fraudTransactionService) {
		this.fraudTransactionService = fraudTransactionService;
	}

	@PostMapping
	public ResponseEntity<FraudCaseIngestResponse> ingest(@RequestBody FraudCaseIngestRequest request) {
		return ResponseEntity.accepted().body(fraudTransactionService.ingest(request));
	}
}
