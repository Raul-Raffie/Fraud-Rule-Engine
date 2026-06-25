package com.raul.fraud_rule_engine_service.service;

public record FraudSignal(
		String code,
		int score,
		String reason) {
}
