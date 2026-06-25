package com.raul.fraud_case_service.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raul.fraud_case_service.domain.FraudTransaction;
import com.raul.fraud_case_service.service.FraudTransactionService;

@RestController
@RequestMapping("/api/fraud-transactions")
public class FraudTransactionController {

	private final FraudTransactionService fraudTransactionService;

	public FraudTransactionController(FraudTransactionService fraudTransactionService) {
		this.fraudTransactionService = fraudTransactionService;
	}

	@GetMapping("/recent")
	public ResponseEntity<Page<FraudTransaction>> recent(Pageable pageable) {
		return ResponseEntity.ok(fraudTransactionService.recent(pageable));
	}
}
