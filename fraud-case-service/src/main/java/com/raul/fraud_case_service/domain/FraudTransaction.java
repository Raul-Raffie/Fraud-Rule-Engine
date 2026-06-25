package com.raul.fraud_case_service.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fraud_transactions", indexes = {
		@Index(name = "idx_fraud_transactions_detected_at", columnList = "detectedAt"),
		@Index(name = "idx_fraud_transactions_account_id", columnList = "accountId"),
		@Index(name = "idx_fraud_transactions_transaction_id", columnList = "transactionId")
})
public class FraudTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, updatable = false)
	private UUID transactionId;

	@Column(nullable = false)
	private String customerId;

	@Column(nullable = false)
	private String accountId;

	@Column(nullable = false)
	private String transactionType;

	@Column(nullable = false)
	private String channel;

	@Column(nullable = false)
	private String merchantName;

	@Column(nullable = true)
	private String merchantCountry;

	@Column(nullable = true)
	private String beneficiaryAccountId;

	@Column(nullable = true)
	private String beneficiaryCountry;

	@Column(nullable = false)
	private String deviceId;

	@Column(nullable = false)
	private String originCountry;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false, length = 8)
	private String currency;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal averageTransactionAmount30d;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal averageMonthlySpend30d;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal accountBalanceBefore;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal accountBalanceAfter;

	@Column(nullable = false)
	private Integer accountAgeDays;

	@Column(nullable = false)
	private Integer transactionsLast10Minutes;

	@Column(nullable = false)
	private Integer transactionsLastHour;

	@Column(nullable = false)
	private boolean salaryDeposit;

	@Column(nullable = false)
	private boolean dormantAccount;

	@Column(nullable = false)
	private boolean fraud;

	@Column(nullable = false)
	private int riskScore;

	@Column(nullable = false)
	private String riskReason;

	@Column(nullable = false, length = 1000)
	private String riskSignals;

	@Column(nullable = false)
	private Instant occurredAt;

	@Column(nullable = false)
	private Instant detectedAt;
}
