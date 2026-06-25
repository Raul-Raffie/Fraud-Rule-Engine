package com.raul.fraud_case_service.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FraudCaseIngestRequest(
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
		String riskReason,
		int riskScore,
		String riskSignals,
		Instant occurredAt,
		Instant detectedAt) {
}
