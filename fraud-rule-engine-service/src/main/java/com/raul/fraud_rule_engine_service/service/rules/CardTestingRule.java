package com.raul.fraud_rule_engine_service.service.rules;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.raul.fraud_rule_engine_service.config.FraudProperties;
import com.raul.fraud_rule_engine_service.service.FraudRule;
import com.raul.fraud_rule_engine_service.service.FraudSignal;
import com.raul.fraud_rule_engine_service.service.state.AccountState;
import com.raul.fraud_rule_engine_service.service.state.TransactionRecord;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

@Component
public class CardTestingRule implements FraudRule {

	private final FraudProperties properties;

	public CardTestingRule(FraudProperties properties) {
		this.properties = properties;
	}

	@Override
	public Optional<FraudSignal> evaluate(TransactionEventRequest event, AccountState state) {
		if (!"card-not-present".equalsIgnoreCase(event.channel())) {
			return Optional.empty();
		}
		var count = state.recentTransactions().stream()
				.filter(record -> withinWindow(record.occurredAt(), event.occurredAt(), properties.rules().cardTestingWindowMinutes()))
				.filter(record -> record.amount().setScale(0, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(properties.rules().lowValueCardLimit())) <= 0)
				.filter(record -> "card-not-present".equalsIgnoreCase(record.channel()))
				.count();
		if (event.amount().setScale(0, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(properties.rules().lowValueCardLimit())) <= 0
				&& count + 1 >= properties.rules().cardTestingAttemptThreshold()) {
			return Optional.of(new FraudSignal("card-testing", 75, "repeated low-value card-not-present attempts"));
		}
		return Optional.empty();
	}

	private boolean withinWindow(java.time.Instant occurredAt, java.time.Instant now, int windowMinutes) {
		return Math.abs(Duration.between(occurredAt, now).toMinutes()) <= windowMinutes;
	}
}
