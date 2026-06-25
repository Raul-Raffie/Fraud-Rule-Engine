package com.raul.fraud_rule_engine_service.service.rules;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.raul.fraud_rule_engine_service.config.FraudProperties;
import com.raul.fraud_rule_engine_service.service.FraudRule;
import com.raul.fraud_rule_engine_service.service.FraudSignal;
import com.raul.fraud_rule_engine_service.service.state.AccountState;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

@Component
public class LargeAmountRule implements FraudRule {

	private final FraudProperties properties;

	public LargeAmountRule(FraudProperties properties) {
		this.properties = properties;
	}

	@Override
	public Optional<FraudSignal> evaluate(TransactionEventRequest event, AccountState state) {
		var multiplier = BigDecimal.valueOf(properties.rules().largeAmountMultiplier());
		var threshold = event.averageTransactionAmount30d().multiply(multiplier);
		if (event.amount().compareTo(threshold) >= 0) {
			return Optional.of(new FraudSignal("large-amount", 30, "large amount far above customer average"));
		}
		return Optional.empty();
	}
}
