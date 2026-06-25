package com.raul.fraud_rule_engine_service.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.raul.fraud_rule_engine_service.service.FraudCaseForwardingService;
import com.raul.fraud_rule_engine_service.service.FraudDetectionService;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventResponse;

@RestController
@RequestMapping("/internal/transactions")
public class FraudRuleEngineRelayController {

	private final FraudDetectionService fraudDetectionService;
	private final FraudCaseForwardingService fraudCaseForwardingService;

	public FraudRuleEngineRelayController(FraudDetectionService fraudDetectionService,
			FraudCaseForwardingService fraudCaseForwardingService) {
		this.fraudDetectionService = fraudDetectionService;
		this.fraudCaseForwardingService = fraudCaseForwardingService;
	}

	@PostMapping
	public ResponseEntity<TransactionEventResponse> relay(@RequestBody TransactionEventRequest transactionEvent) {
		var finding = fraudDetectionService.detect(transactionEvent);
		if (finding.fraud()) {
			fraudCaseForwardingService.forwardFraud(transactionEvent, finding);
			return ResponseEntity.ok(new TransactionEventResponse("fraud", finding.reason()));
		}
		return ResponseEntity.ok(new TransactionEventResponse("approved", finding.reason()));
	}
}
