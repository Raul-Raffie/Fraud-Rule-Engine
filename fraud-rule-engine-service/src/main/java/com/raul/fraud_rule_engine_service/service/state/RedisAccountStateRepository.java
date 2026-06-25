package com.raul.fraud_rule_engine_service.service.state;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.raul.fraud_rule_engine_service.service.AccountStateRepository;

@Repository
@ConditionalOnBean(RedisTemplate.class)
public class RedisAccountStateRepository implements AccountStateRepository {

	private final RedisTemplate<String, AccountState> redisTemplate;

	public RedisAccountStateRepository(RedisTemplate<String, AccountState> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public AccountState load(String accountId) {
		var state = redisTemplate.opsForValue().get(key(accountId));
		return state == null ? new AccountState() : state;
	}

	@Override
	public void save(String accountId, AccountState state) {
		redisTemplate.opsForValue().set(key(accountId), state);
	}

	private String key(String accountId) {
		return "fraud:account-state:" + accountId;
	}
}
