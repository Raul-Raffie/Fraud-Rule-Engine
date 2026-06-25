package com.raul.fraud_case_service.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.raul.fraud_case_service.domain.FraudTransaction;
import com.raul.fraud_case_service.repository.FraudTransactionRepository;
import com.raul.fraud_case_service.web.dto.FraudCaseIngestRequest;
import com.raul.fraud_case_service.web.dto.FraudCaseIngestResponse;

@Service
public class FraudTransactionService {

	private final FraudTransactionRepository repository;

	public FraudTransactionService(FraudTransactionRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public FraudCaseIngestResponse ingest(FraudCaseIngestRequest request) {
		var occurredAt = request.occurredAt() == null ? Instant.now() : request.occurredAt();
		var detectedAt = request.detectedAt() == null ? Instant.now() : request.detectedAt();
		var saved = repository.save(FraudTransaction.builder()
				.transactionId(request.transactionId())
				.customerId(request.customerId())
				.accountId(request.accountId())
				.transactionType(request.transactionType())
				.channel(request.channel())
				.merchantName(request.merchantName())
				.merchantCountry(request.merchantCountry())
				.beneficiaryAccountId(request.beneficiaryAccountId())
				.beneficiaryCountry(request.beneficiaryCountry())
				.deviceId(request.deviceId())
				.originCountry(request.originCountry())
				.amount(request.amount())
				.currency(request.currency())
				.averageTransactionAmount30d(request.averageTransactionAmount30d())
				.averageMonthlySpend30d(request.averageMonthlySpend30d())
				.accountBalanceBefore(request.accountBalanceBefore())
				.accountBalanceAfter(request.accountBalanceAfter())
				.accountAgeDays(request.accountAgeDays())
				.transactionsLast10Minutes(request.transactionsLast10Minutes())
				.transactionsLastHour(request.transactionsLastHour())
				.salaryDeposit(request.salaryDeposit())
				.dormantAccount(request.dormantAccount())
				.fraud(true)
				.riskScore(request.riskScore())
				.riskReason(request.riskReason())
				.riskSignals(request.riskSignals())
				.occurredAt(occurredAt)
				.detectedAt(detectedAt)
				.build());
		return new FraudCaseIngestResponse("accepted", saved.getId().toString());
	}

	@Transactional(readOnly = true)
	public Page<FraudTransaction> recent(Pageable pageable) {
		return repository.findAllByOrderByDetectedAtDesc(pageable);
	}
}
