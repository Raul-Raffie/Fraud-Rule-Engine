package com.raul.fraud_rule_engine_service.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "fraud")
public record FraudProperties(
		@Min(1) int threshold,
		@NotNull RuleProperties rules,
		@NotNull Map<@NotBlank String, Integer> countryRiskScores,
		@NotNull Map<@NotBlank String, GeoLocation> geoLocations) {

	public record RuleProperties(
			@Min(1) long travelMaxSpeedKmh,
			@Min(1) int cardTestingWindowMinutes,
			@Min(1) int cardTestingAttemptThreshold,
			@Min(1) int dormantDays,
			@Min(1) int largeAmountMultiplier,
			@Min(1) int rapidActivityTx10Threshold,
			@Min(1) int rapidActivityTx60Threshold,
			@Min(1) int newPayeeMultiplier,
			@Min(1) int salaryDrainWindowMinutes,
			@Min(1) int salaryDrainTransferThreshold,
			@Min(1) int lowValueCardLimit,
			@Min(0) int highRiskCountryThreshold) {
	}

	public record GeoLocation(double latitude, double longitude) {
	}
}
