package com.raul.fraud_rule_engine_service.service;

import com.raul.fraud_rule_engine_service.service.state.AccountState;

public interface AccountStateRepository {
	AccountState load(String accountId);

	void save(String accountId, AccountState state);
}
