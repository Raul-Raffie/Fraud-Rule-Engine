package com.raul.fraud_rule_engine_service.service;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.raul.fraud_rule_engine_service.client.FraudCaseClient;
import com.raul.fraud_rule_engine_service.client.dto.FraudCaseRequest;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

@Service
public class FraudCaseForwardingService {

	private static final Logger log = LoggerFactory.getLogger(FraudCaseForwardingService.class);

	private final FraudCaseClient fraudCaseClient;

	public FraudCaseForwardingService(FraudCaseClient fraudCaseClient) {
		this.fraudCaseClient = fraudCaseClient;
	}

	@Async
	public void forwardFraud(TransactionEventRequest event, FraudFinding finding) {
		try {
			var request = new FraudCaseRequest(
					event.transactionId(),
					event.customerId(),
					event.accountId(),
					event.transactionType(),
					event.channel(),
					event.merchantName(),
					event.merchantCountry(),
					event.beneficiaryAccountId(),
					event.beneficiaryCountry(),
					event.deviceId(),
					event.originCountry(),
					event.amount(),
					event.currency(),
					event.averageTransactionAmount30d(),
					event.averageMonthlySpend30d(),
					event.accountBalanceBefore(),
					event.accountBalanceAfter(),
					event.accountAgeDays(),
					event.transactionsLast10Minutes(),
					event.transactionsLastHour(),
					event.salaryDeposit(),
					event.dormantAccount(),
					finding.reason(),
					finding.score(),
					finding.signals().stream().map(FraudSignal::code).collect(Collectors.joining(",")),
					event.occurredAt(),
					Instant.now());
			fraudCaseClient.createCase(request);
		} catch (Exception ex) {
			log.error("Failed to forward fraud case for transaction {}", event.transactionId(), ex);
		}
	}
}
