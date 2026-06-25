package com.raul.fraud_rule_engine_service.service.state;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.raul.fraud_rule_engine_service.service.AccountStateRepository;

@Repository
@Primary
public class InMemoryAccountStateRepository implements AccountStateRepository {

	private final ConcurrentHashMap<String, AccountState> states = new ConcurrentHashMap<>();

	@Override
	public AccountState load(String accountId) {
		return states.computeIfAbsent(accountId, ignored -> new AccountState());
	}

	@Override
	public void save(String accountId, AccountState state) {
		states.put(accountId, state);
	}
}
