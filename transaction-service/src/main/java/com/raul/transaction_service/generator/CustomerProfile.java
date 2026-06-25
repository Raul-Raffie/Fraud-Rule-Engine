package com.raul.transaction_service.generator;

import java.math.BigDecimal;
import java.util.List;

public record CustomerProfile(
		String profileId,
		String customerType,
		String accountId,
		String homeCountry,
		String currency,
		BigDecimal averageTransactionAmount,
		BigDecimal typicalMonthlySpend,
		List<String> preferredCountries,
		List<String> preferredDevices,
		List<Integer> preferredHours,
		List<String> typicalCategories,
		boolean salaryCustomer,
		boolean dormant) {
}
