package com.raul.fraud_rule_engine_service.service.state;

import java.math.BigDecimal;
import java.time.Instant;

import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

public record TransactionRecord(
		Instant occurredAt,
		BigDecimal amount,
		String channel,
		String transactionType,
		String merchantCountry,
		String beneficiaryAccountId,
		String deviceId,
		boolean salaryDeposit,
		boolean dormantAccount) {

	static TransactionRecord from(TransactionEventRequest event) {
		return new TransactionRecord(
				event.occurredAt(),
				event.amount(),
				event.channel(),
				event.transactionType(),
				event.merchantCountry(),
				event.beneficiaryAccountId(),
				event.deviceId(),
				event.salaryDeposit(),
				event.dormantAccount());
	}
}
