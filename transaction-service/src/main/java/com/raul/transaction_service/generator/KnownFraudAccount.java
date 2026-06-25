package com.raul.transaction_service.generator;

public record KnownFraudAccount(
		String accountId,
		String country,
		String label) {
}
