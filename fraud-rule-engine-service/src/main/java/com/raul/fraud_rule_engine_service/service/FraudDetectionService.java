package com.raul.fraud_rule_engine_service.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.raul.fraud_rule_engine_service.config.FraudProperties;
import com.raul.fraud_rule_engine_service.service.state.AccountState;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudDetectionService {

	private static final FraudSignal APPROVED_SIGNAL = new FraudSignal("approved", 0, "approved");

	private final List<FraudRule> fraudRules;
	private final AccountStateRepository accountStateRepository;
	private final GeoLocationService geoLocationService;
	private final FraudProperties fraudProperties;

	public FraudFinding detect(TransactionEventRequest event) {
		var state = accountStateRepository.load(event.accountId());
		var signals = evaluateSignals(event, state);
		var score = totalScore(signals);
		var topSignal = topSignal(signals);
		var fraud = score >= fraudProperties.threshold();

		logDecision(event, score, fraud, topSignal);

		applyState(event, state);
		accountStateRepository.save(event.accountId(), state);

		return new FraudFinding(fraud, score, topSignal.reason(), signals);
	}

	private List<FraudSignal> evaluateSignals(TransactionEventRequest event, AccountState state) {
		return fraudRules.stream()
				.map(rule -> rule.evaluate(event, state))
				.flatMap(Optional::stream)
				.toList();
	}

	private int totalScore(List<FraudSignal> signals) {
		return signals.stream().mapToInt(FraudSignal::score).sum();
	}

	private FraudSignal topSignal(List<FraudSignal> signals) {
		return signals.stream()
				.max(Comparator.comparingInt(FraudSignal::score).thenComparing(FraudSignal::code))
				.orElse(APPROVED_SIGNAL);
	}

	private void logDecision(TransactionEventRequest event, int score, boolean fraud, FraudSignal topSignal) {
		log.info("fraud_decision accountId={} transactionId={} score={} threshold={} fraud={} topSignal={}",
				event.accountId(), event.transactionId(), score, fraudProperties.threshold(), fraud, topSignal.code());
	}

	private void applyState(TransactionEventRequest event, AccountState state) {
		var country = firstNonBlank(event.merchantCountry(), event.originCountry(), event.beneficiaryCountry());
		var location = geoLocationService.lookup(country).orElse(null);
		state.record(event, location == null ? null : location.latitude(), location == null ? null : location.longitude());
	}

	private String firstNonBlank(String... values) {
		for (var value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return null;
	}
}
