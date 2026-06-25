package com.raul.fraud_rule_engine_service.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionEventRequest(
		UUID transactionId,
		String customerId,
		String accountId,
		String transactionType,
		String channel,
		String merchantName,
		String merchantCountry,
		String beneficiaryAccountId,
		String beneficiaryCountry,
		String deviceId,
		boolean newDevice,
		String originCountry,
		BigDecimal amount,
		String currency,
		BigDecimal averageTransactionAmount30d,
		BigDecimal averageMonthlySpend30d,
		BigDecimal accountBalanceBefore,
		BigDecimal accountBalanceAfter,
		Integer accountAgeDays,
		Integer transactionsLast10Minutes,
		Integer transactionsLastHour,
		boolean salaryDeposit,
		boolean dormantAccount,
		Instant occurredAt,
		Instant previousTransactionAt) {
}
