package com.raul.fraud_rule_engine_service.service;

import java.util.List;

public record FraudFinding(
		boolean fraud,
		int score,
		String reason,
		List<FraudSignal> signals) {
}
