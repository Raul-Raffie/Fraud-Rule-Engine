package com.raul.transaction_service.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.raul.transaction_service.client.dto.TransactionEventRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionEntity extends BaseEntity {

	@Column(nullable = false, unique = true)
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

	@Column(nullable = false)
	private String merchantCountry;

	@Column
	private String beneficiaryAccountId;

	@Column
	private String beneficiaryCountry;

	@Column(nullable = false)
	private String deviceId;

	@Column(nullable = false)
	private boolean newDevice;

	@Column(nullable = false)
	private String originCountry;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false)
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
	private Instant occurredAt;

	@Column
	private Instant previousTransactionAt;

	@Column(nullable = false)
	private String status;

	@Column
	private String message;

	public static TransactionEntity fromRequest(TransactionEventRequest request) {
		var entity = new TransactionEntity();
		entity.setTransactionId(request.transactionId());
		entity.setCustomerId(request.customerId());
		entity.setAccountId(request.accountId());
		entity.setTransactionType(request.transactionType());
		entity.setChannel(request.channel());
		entity.setMerchantName(request.merchantName());
		entity.setMerchantCountry(request.merchantCountry());
		entity.setBeneficiaryAccountId(request.beneficiaryAccountId());
		entity.setBeneficiaryCountry(request.beneficiaryCountry());
		entity.setDeviceId(request.deviceId());
		entity.setNewDevice(request.newDevice());
		entity.setOriginCountry(request.originCountry());
		entity.setAmount(request.amount());
		entity.setCurrency(request.currency());
		entity.setAverageTransactionAmount30d(request.averageTransactionAmount30d());
		entity.setAverageMonthlySpend30d(request.averageMonthlySpend30d());
		entity.setAccountBalanceBefore(request.accountBalanceBefore());
		entity.setAccountBalanceAfter(request.accountBalanceAfter());
		entity.setAccountAgeDays(request.accountAgeDays());
		entity.setTransactionsLast10Minutes(request.transactionsLast10Minutes());
		entity.setTransactionsLastHour(request.transactionsLastHour());
		entity.setSalaryDeposit(request.salaryDeposit());
		entity.setDormantAccount(request.dormantAccount());
		entity.setOccurredAt(request.occurredAt());
		entity.setPreviousTransactionAt(request.previousTransactionAt());
		entity.setStatus("PENDING");
		entity.setMessage(null);
		return entity;
	}
}
