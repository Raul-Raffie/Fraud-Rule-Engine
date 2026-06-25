package com.raul.transaction_service.generator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "transaction.generator")
public record TransactionGeneratorProperties(
		@Min(1) int scheduleDelayMs,
		@Min(1) @Max(100) int fraudPercentage,
		@NotNull Boolean enabled) {
}
