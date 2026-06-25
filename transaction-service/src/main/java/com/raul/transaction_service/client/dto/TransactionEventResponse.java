package com.raul.transaction_service.client.dto;

public record TransactionEventResponse(
		String status,
		String message) {
}
