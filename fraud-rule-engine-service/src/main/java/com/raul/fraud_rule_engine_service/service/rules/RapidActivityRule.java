package com.raul.fraud_rule_engine_service.service.rules;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.raul.fraud_rule_engine_service.config.FraudProperties;
import com.raul.fraud_rule_engine_service.service.FraudRule;
import com.raul.fraud_rule_engine_service.service.FraudSignal;
import com.raul.fraud_rule_engine_service.service.state.AccountState;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

@Component
public class RapidActivityRule implements FraudRule {

	private final FraudProperties properties;

	public RapidActivityRule(FraudProperties properties) {
		this.properties = properties;
	}

	@Override
	public Optional<FraudSignal> evaluate(TransactionEventRequest event, AccountState state) {
		var tx10 = event.transactionsLast10Minutes() == null ? 0 : event.transactionsLast10Minutes();
		var tx60 = event.transactionsLastHour() == null ? 0 : event.transactionsLastHour();
		if (tx10 >= properties.rules().rapidActivityTx10Threshold() || tx60 >= properties.rules().rapidActivityTx60Threshold()) {
			return Optional.of(new FraudSignal("rapid-transactions", 65, "rapid transactions in a short window"));
		}
		return Optional.empty();
	}
}
