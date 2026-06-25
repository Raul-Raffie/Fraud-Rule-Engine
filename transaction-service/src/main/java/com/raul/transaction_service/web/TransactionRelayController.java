package com.raul.transaction_service.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raul.transaction_service.client.dto.TransactionEventRequest;
import com.raul.transaction_service.client.dto.TransactionEventResponse;
import com.raul.transaction_service.service.TransactionSubmissionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRelayController {

	private final TransactionSubmissionService transactionSubmissionService;

	public TransactionRelayController(TransactionSubmissionService transactionSubmissionService) {
		this.transactionSubmissionService = transactionSubmissionService;
	}

	@PostMapping("/relay")
	public ResponseEntity<TransactionEventResponse> relay(@RequestBody TransactionEventRequest request) {
		return ResponseEntity.ok(transactionSubmissionService.saveAndSubmit(request));
	}

	@PostMapping("/sample")
	public ResponseEntity<TransactionEventResponse> sample() {
		var request = new TransactionEventRequest(
				UUID.randomUUID(),
				"customer-sample-001",
				"acct-001",
				"purchase",
				"card-not-present",
				"Checkers",
				"ZA",
				null,
				"ZA",
				"device-sample-001",
				false,
				"ZA",
				java.math.BigDecimal.valueOf(125.75),
				"ZAR",
				java.math.BigDecimal.valueOf(140.00),
				java.math.BigDecimal.valueOf(4200.00),
				java.math.BigDecimal.valueOf(2100.00),
				java.math.BigDecimal.valueOf(1974.25),
				180,
				2,
				4,
				false,
				false,
				java.time.Instant.now(),
				java.time.Instant.now().minusSeconds(1800));
		return ResponseEntity.ok(transactionSubmissionService.saveAndSubmit(request));
	}
}
