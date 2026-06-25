package com.raul.fraud_rule_engine_service.service;

import java.util.Optional;

import com.raul.fraud_rule_engine_service.service.state.AccountState;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

public interface FraudRule {
	Optional<FraudSignal> evaluate(TransactionEventRequest event, AccountState state);
}
