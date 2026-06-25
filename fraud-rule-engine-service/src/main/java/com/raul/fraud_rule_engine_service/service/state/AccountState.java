package com.raul.fraud_rule_engine_service.service.state;

import java.time.Instant;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

public class AccountState {
	private final Set<String> knownDevices = ConcurrentHashMap.newKeySet();
	private final Set<String> knownBeneficiaries = ConcurrentHashMap.newKeySet();
	private final Deque<TransactionRecord> recentTransactions = new ConcurrentLinkedDeque<>();
	private final Deque<Instant> salaryDeposits = new ConcurrentLinkedDeque<>();
	private final AtomicReference<Instant> firstSeenAt = new AtomicReference<>();
	private final AtomicReference<Instant> lastTransactionAt = new AtomicReference<>();
	private final AtomicReference<String> lastCountry = new AtomicReference<>();
	private final AtomicReference<Double> lastLatitude = new AtomicReference<>();
	private final AtomicReference<Double> lastLongitude = new AtomicReference<>();

	public void record(TransactionEventRequest event, Double latitude, Double longitude) {
		firstSeenAt.compareAndSet(null, event.occurredAt());
		lastTransactionAt.set(event.occurredAt());
		if (event.merchantCountry() != null) {
			lastCountry.set(event.merchantCountry());
		} else if (event.originCountry() != null) {
			lastCountry.set(event.originCountry());
		}
		if (latitude != null && longitude != null) {
			lastLatitude.set(latitude);
			lastLongitude.set(longitude);
		}
		if (event.deviceId() != null) {
			knownDevices.add(event.deviceId());
		}
		if (event.beneficiaryAccountId() != null) {
			knownBeneficiaries.add(event.beneficiaryAccountId());
		}
		if (event.salaryDeposit()) {
			salaryDeposits.addLast(event.occurredAt());
		}
		recentTransactions.addLast(TransactionRecord.from(event));
		while (recentTransactions.size() > 100) {
			recentTransactions.pollFirst();
		}
		while (salaryDeposits.size() > 20) {
			salaryDeposits.pollFirst();
		}
	}

	public Set<String> knownDevices() {
		return knownDevices;
	}

	public Set<String> knownBeneficiaries() {
		return knownBeneficiaries;
	}

	public Deque<TransactionRecord> recentTransactions() {
		return recentTransactions;
	}

	public Deque<Instant> salaryDeposits() {
		return salaryDeposits;
	}

	public Instant firstSeenAt() {
		return firstSeenAt.get();
	}

	public Instant lastTransactionAt() {
		return lastTransactionAt.get();
	}

	public String lastCountry() {
		return lastCountry.get();
	}

	public Double lastLatitude() {
		return lastLatitude.get();
	}

	public Double lastLongitude() {
		return lastLongitude.get();
	}
}
