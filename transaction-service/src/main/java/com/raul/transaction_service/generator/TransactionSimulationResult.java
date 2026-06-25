package com.raul.transaction_service.generator;

import com.raul.transaction_service.client.dto.TransactionEventRequest;
import com.raul.transaction_service.client.dto.TransactionEventResponse;

public record TransactionSimulationResult(
		TransactionEventRequest request,
		TransactionEventResponse response,
		boolean fraud,
		FraudScenario scenario,
		String customerProfileId,
		String reason) {
}
