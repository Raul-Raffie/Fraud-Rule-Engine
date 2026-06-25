package com.raul.fraud_rule_engine_service.service.rules;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.raul.fraud_rule_engine_service.config.FraudProperties;
import com.raul.fraud_rule_engine_service.service.FraudRule;
import com.raul.fraud_rule_engine_service.service.FraudSignal;
import com.raul.fraud_rule_engine_service.service.GeoLocationService;
import com.raul.fraud_rule_engine_service.service.state.AccountState;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

@Component
public class ImpossibleTravelRule implements FraudRule {

	private final GeoLocationService geoLocationService;
	private final FraudProperties properties;

	public ImpossibleTravelRule(GeoLocationService geoLocationService, FraudProperties properties) {
		this.geoLocationService = geoLocationService;
		this.properties = properties;
	}

	@Override
	public Optional<FraudSignal> evaluate(TransactionEventRequest event, AccountState state) {
		var lastAt = state.lastTransactionAt();
		var lastLat = state.lastLatitude();
		var lastLon = state.lastLongitude();
		var current = currentLocation(event);
		if (lastAt == null || lastLat == null || lastLon == null || current.isEmpty()) {
			return Optional.empty();
		}
		var deltaHours = Math.max(Duration.between(lastAt, event.occurredAt()).toMillis() / 3600000.0d, 0.01d);
		var distanceKm = geoLocationService.calculateDistanceKm(lastLat, lastLon, current.get().latitude(), current.get().longitude());
		var speed = distanceKm / deltaHours;
		if (speed > properties.rules().travelMaxSpeedKmh()) {
			return Optional.of(new FraudSignal("impossible-travel", 70, "impossible travel across distant locations"));
		}
		return Optional.empty();
	}

	private Optional<GeoLocationService.Location> currentLocation(TransactionEventRequest event) {
		return geoLocationService.lookup(firstNonBlank(event.merchantCountry(), event.originCountry(), event.beneficiaryCountry()));
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
