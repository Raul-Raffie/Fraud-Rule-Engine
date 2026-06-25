package com.raul.transaction_service.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.raul.transaction_service.client.dto.TransactionEventRequest;
import com.raul.transaction_service.client.dto.TransactionEventResponse;
import com.raul.transaction_service.service.TransactionSubmissionService;

@Service
public class TransactionGeneratorService {

	private static final ZoneId LOCAL_ZONE = ZoneId.systemDefault();
	private static final List<CustomerProfile> CUSTOMER_PROFILES = List.of(
			new CustomerProfile("student-001", "student", "acct-student-001", "ZA", "ZAR",
					BigDecimal.valueOf(120.00), BigDecimal.valueOf(3500.00),
					List.of("ZA", "BW", "NA"), List.of("mobile-android", "laptop-chrome"), List.of(9, 12, 18, 20),
					List.of("food", "transport", "entertainment"), false, false),
			new CustomerProfile("office-001", "office worker", "acct-office-001", "ZA", "ZAR",
					BigDecimal.valueOf(280.00), BigDecimal.valueOf(24000.00),
					List.of("ZA", "BW", "KE", "NG"), List.of("laptop-windows", "mobile-android"), List.of(7, 8, 13, 18),
					List.of("groceries", "travel", "retail"), false, false),
			new CustomerProfile("business-001", "business owner", "acct-business-001", "ZA", "ZAR",
					BigDecimal.valueOf(2400.00), BigDecimal.valueOf(180000.00),
					List.of("ZA", "AE", "GB", "AE"), List.of("laptop-mac", "tablet-ipad", "mobile-iphone"), List.of(8, 10, 14, 17),
					List.of("suppliers", "travel", "operations"), false, false),
			new CustomerProfile("retiree-001", "retiree", "acct-retiree-001", "ZA", "ZAR",
					BigDecimal.valueOf(75.00), BigDecimal.valueOf(4200.00),
					List.of("ZA", "NA", "BW"), List.of("tablet-ipad", "mobile-android"), List.of(10, 11, 15, 19),
					List.of("health", "groceries", "utilities"), true, false),
			new CustomerProfile("salary-001", "worker", "acct-salary-001", "ZA", "ZAR",
					BigDecimal.valueOf(320.00), BigDecimal.valueOf(28000.00),
					List.of("ZA", "BW", "NA"), List.of("mobile-android", "laptop-windows"), List.of(8, 12, 18, 21),
					List.of("rent", "groceries", "fuel"), true, false),
			new CustomerProfile("dormant-001", "retiree", "acct-dormant-001", "ZA", "ZAR",
					BigDecimal.valueOf(90.00), BigDecimal.valueOf(1500.00),
					List.of("ZA", "BW"), List.of("mobile-android"), List.of(10, 14, 16),
					List.of("health", "groceries"), false, true));

	//Todo: Maybe move this into springboot startup configuration to automatically populate and check the database if these accounts are there
	private static final List<KnownFraudAccount> KNOWN_FRAUD_ACCOUNTS = List.of(
			new KnownFraudAccount("fraud-acct-001", "NG", "mule-network-1"),
			new KnownFraudAccount("fraud-acct-002", "LT", "carding-payout"),
			new KnownFraudAccount("fraud-acct-003", "BR", "synthetic-payee"),
			new KnownFraudAccount("fraud-acct-004", "RU", "high-risk-merchant"));
	private static final List<String> COUNTRIES = List.of("ZA", "US", "GB", "AE", "NZ", "CA", "SG", "FR", "DE", "IE", "NG", "BR");

	private final TransactionSubmissionService transactionSubmissionService;
	private final TransactionGeneratorProperties properties;
	private final Random random = new Random();

	public TransactionGeneratorService(TransactionSubmissionService transactionSubmissionService, TransactionGeneratorProperties properties) {
		this.transactionSubmissionService = transactionSubmissionService;
		this.properties = properties;
	}

	public TransactionSimulationResult generateAndSubmitRandomTransaction() {
		var profile = pickProfile();
		var fraud = random.nextInt(100) < properties.fraudPercentage();
		var scenario = fraud ? pickRandomScenario() : null;
		var request = fraud ? buildFraudTransaction(profile, scenario) : buildLegitimateTransaction(profile);
		var response = transactionSubmissionService.saveAndSubmit(request);
		return new TransactionSimulationResult(request, response, fraud, scenario, profile.profileId(), response.message());
	}

	public TransactionSimulationResult generateAndSubmitFraudScenario(FraudScenario scenario) {
		var profile = pickProfile();
		var request = buildFraudTransaction(profile, scenario);
		var response = transactionSubmissionService.saveAndSubmit(request);
		return new TransactionSimulationResult(request, response, true, scenario, profile.profileId(), response.message());
	}

	public TransactionSimulationResult generateAndSubmitLegitimateTransaction() {
		var profile = pickProfile();
		var request = buildLegitimateTransaction(profile);
		var response = transactionSubmissionService.saveAndSubmit(request);
		return new TransactionSimulationResult(request, response, false, null, profile.profileId(), response.message());
	}

	public List<CustomerProfile> customerProfiles() {
		return CUSTOMER_PROFILES;
	}

	public List<KnownFraudAccount> knownFraudAccounts() {
		return KNOWN_FRAUD_ACCOUNTS;
	}

	private CustomerProfile pickProfile() {
		return CUSTOMER_PROFILES.get(random.nextInt(CUSTOMER_PROFILES.size()));
	}

	private FraudScenario pickRandomScenario() {
		var scenarios = FraudScenario.values();
		return scenarios[random.nextInt(scenarios.length)];
	}

	private TransactionEventRequest buildLegitimateTransaction(CustomerProfile profile) {
		var amount = normalAmount(profile.averageTransactionAmount(), 0.35, 0.9);
		var timestamp = pickTypicalTimestamp(profile);
		var channel = pickChannel(false);
		return buildRequest(profile, "purchase", channel, pickMerchant(profile, channel),
				pickCountry(profile.preferredCountries()), null, profile.homeCountry(), pickOne(profile.preferredDevices()),
				false, profile.homeCountry(), amount, profile.currency(), profile.averageTransactionAmount(),
				profile.typicalMonthlySpend(), balanceBefore(profile, amount), balanceBefore(profile, amount).subtract(amount).max(BigDecimal.ZERO),
				randomAccountAge(profile), random.nextInt(3), random.nextInt(3), false, profile.dormant(), timestamp, randomHistory(profile, timestamp));
	}

	private TransactionEventRequest buildFraudTransaction(CustomerProfile profile, FraudScenario scenario) {
		return switch (scenario) {
			case LARGE_AMOUNT_ABOVE_AVERAGE -> {
				var occurredAt = pickOddHoursInstant();
				var amount = profile.averageTransactionAmount().multiply(BigDecimal.valueOf(15)).setScale(2, RoundingMode.HALF_UP);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "wire", "online-banking", "Moonlight Imports",
						distantCountry(profile.homeCountry()), "beneficiary-" + UUID.randomUUID(), distantCountry(profile.homeCountry()),
						"device-" + UUID.randomUUID(), false, profile.homeCountry(), amount, profile.currency(),
						profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 0, 0, false, false,
						occurredAt, randomHistory(profile, occurredAt));
			}
			case ODD_HOURS_TRANSACTION -> {
				var occurredAt = pickOddHoursInstant();
				var amount = normalAmount(profile.averageTransactionAmount(), 0.8, 2.2);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "purchase", "card-not-present", pickMerchant(profile, "card"),
						profile.homeCountry(), null, profile.homeCountry(), pickOne(profile.preferredDevices()), false, profile.homeCountry(),
						amount, profile.currency(), profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 0, 0, false, false, occurredAt,
						randomHistory(profile, occurredAt));
			}
			case KNOWN_FRAUD_ACCOUNT -> {
				var occurredAt = recentInstant();
				var amount = normalAmount(profile.averageTransactionAmount(), 2.5, 8.0);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "transfer", "online-banking", "External Payee", distantCountry(profile.homeCountry()),
						knownFraudAccountReference(), "NG", pickOne(profile.preferredDevices()), false, profile.homeCountry(), amount,
						profile.currency(), profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 0, 0, false, false, occurredAt,
						randomHistory(profile, occurredAt));
			}
			case IMPOSSIBLE_TRAVEL -> {
				var occurredAt = recentInstant();
				var amount = normalAmount(profile.averageTransactionAmount(), 0.9, 3.0);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "card", "card-present", "Travel Purchase", distantCountry(profile.homeCountry()), null,
						distantCountry(profile.homeCountry()), pickOne(profile.preferredDevices()), false, profile.homeCountry(), amount,
						profile.currency(), profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 0, 0, false, false, occurredAt,
						recentInstant());
			}
			case NEW_DEVICE_USAGE -> {
				var occurredAt = recentInstant();
				var amount = normalAmount(profile.averageTransactionAmount(), 0.6, 2.0);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "card-not-present", "mobile-banking", "Retail Purchase", profile.homeCountry(), null,
						profile.homeCountry(), "device-" + UUID.randomUUID(), true, profile.homeCountry(), amount, profile.currency(),
						profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 0, 0, false, false, occurredAt,
						randomHistory(profile, occurredAt));
			}
			case RAPID_TRANSACTIONS -> {
				var occurredAt = recentInstant();
				var amount = rapidBurstAmount();
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "card", "card-not-present", "Quick POS", profile.homeCountry(), null, profile.homeCountry(),
						pickOne(profile.preferredDevices()), false, profile.homeCountry(), amount, profile.currency(),
						profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 5, 12, false, false, occurredAt,
						randomHistory(profile, occurredAt));
			}
			case CARD_TESTING -> {
				var occurredAt = recentInstant();
				var amount = BigDecimal.valueOf(List.of(1, 2, 5, 10).get(random.nextInt(4))).setScale(2);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "card", "card-not-present", "Card Verification", profile.homeCountry(), null,
						profile.homeCountry(), pickOne(profile.preferredDevices()), false, profile.homeCountry(), amount, profile.currency(),
						profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 1, 1, false, false, occurredAt,
						randomHistory(profile, occurredAt));
			}
			case NEW_PAYEE_LARGE_TRANSFER -> {
				var occurredAt = recentInstant();
				var amount = profile.averageTransactionAmount().multiply(BigDecimal.valueOf(12)).setScale(2, RoundingMode.HALF_UP);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "transfer", "online-banking", "New Payee", profile.homeCountry(),
						"payee-" + UUID.randomUUID(), profile.homeCountry(), "device-" + UUID.randomUUID(), false, profile.homeCountry(),
						amount, profile.currency(), profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 0, 0, false, false, occurredAt,
						randomHistory(profile, occurredAt));
			}
			case SALARY_DRAIN -> {
				var occurredAt = depositDayInstant();
				var amount = profile.typicalMonthlySpend().multiply(BigDecimal.valueOf(0.95)).setScale(2, RoundingMode.HALF_UP);
				var balanceBefore = balanceBefore(profile, amount).add(amount);
				yield buildRequest(profile, "transfer", "mobile-banking", "ATM Withdrawal", profile.homeCountry(), null,
						profile.homeCountry(), pickOne(profile.preferredDevices()), false, profile.homeCountry(), amount, profile.currency(),
						profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), randomAccountAge(profile), 0, 0, true, false, occurredAt,
						randomHistory(profile, occurredAt));
			}
			case DORMANT_ACCOUNT_ACTIVATED -> {
				var occurredAt = Instant.now().minus(2, ChronoUnit.MINUTES);
				var amount = BigDecimal.valueOf(1200.00);
				var balanceBefore = balanceBefore(profile, amount);
				yield buildRequest(profile, "transfer", "online-banking", "Reactivation Transfer", profile.homeCountry(), null,
						profile.homeCountry(), pickOne(profile.preferredDevices()), false, profile.homeCountry(), amount, profile.currency(),
						profile.averageTransactionAmount(), profile.typicalMonthlySpend(), balanceBefore,
						balanceBefore.subtract(amount).max(BigDecimal.ZERO), 400, 0, 0, false, true, occurredAt, null);
			}
		};
	}

	private String knownFraudAccountReference() {
		return "fraud-account:" + KNOWN_FRAUD_ACCOUNTS.get(random.nextInt(KNOWN_FRAUD_ACCOUNTS.size())).accountId();
	}

	private TransactionEventRequest buildRequest(CustomerProfile profile, String transactionType, String channel,
			String merchantName, String merchantCountry, String beneficiaryAccountId, String beneficiaryCountry,
			String deviceId, boolean newDevice, String originCountry, BigDecimal amount, String currency,
			BigDecimal averageTransactionAmount30d, BigDecimal averageMonthlySpend30d, BigDecimal accountBalanceBefore,
			BigDecimal accountBalanceAfter, Integer accountAgeDays, Integer last10Minutes, Integer lastHour,
			boolean salaryDeposit, boolean dormantAccount, Instant occurredAt, Instant previousTransactionAt) {
		return new TransactionEventRequest(
				UUID.randomUUID(),
				profile.profileId(),
				profile.accountId(),
				transactionType,
				channel,
				merchantName,
				merchantCountry,
				beneficiaryAccountId,
				beneficiaryCountry,
				deviceId,
				newDevice,
				originCountry,
				amount,
				currency,
				averageTransactionAmount30d,
				averageMonthlySpend30d,
				accountBalanceBefore,
				accountBalanceAfter,
				accountAgeDays,
				last10Minutes,
				lastHour,
				salaryDeposit,
				dormantAccount,
				occurredAt,
				previousTransactionAt);
	}

	private Instant pickTypicalTimestamp(CustomerProfile profile) {
		var hour = profile.preferredHours().get(random.nextInt(profile.preferredHours().size()));
		return ZonedDateTime.of(LocalDateTime.now(LOCAL_ZONE).withHour(hour).withMinute(random.nextInt(60))
				.withSecond(random.nextInt(60)).withNano(0), LOCAL_ZONE).toInstant();
	}

	//I used 1am to 6am for odd hours
	private Instant pickOddHoursInstant() {
		var hour = random.nextInt(5);
		return ZonedDateTime.of(LocalDateTime.now(LOCAL_ZONE).withHour(hour).withMinute(random.nextInt(60))
				.withSecond(random.nextInt(60)).withNano(0), LOCAL_ZONE).toInstant();
	}

	private Instant recentInstant() {
		return Instant.now().minus(random.nextInt(20), ChronoUnit.MINUTES);
	}

	private Instant depositDayInstant() {
		return Instant.now().minus(random.nextInt(30), ChronoUnit.MINUTES);
	}

	private String distantCountry(String homeCountry) {
		var candidates = COUNTRIES.stream().filter(country -> !country.equals(homeCountry)).toList();
		return candidates.isEmpty() ? "US" : candidates.get(random.nextInt(candidates.size()));
	}

	private String pickMerchant(CustomerProfile profile, String channel) {
		return switch (channel) {
			case "wire" -> "Supplier Settlement";
			case "transfer" -> "EFT Transfer";
			case "card" -> pickOne(profile.typicalCategories());
			default -> "Retail Checkout";
		};
	}

	private <T> T pickOne(List<T> values) {
		return values.get(random.nextInt(values.size()));
	}

	private String pickCountry(List<String> countries) {
		return countries.get(random.nextInt(countries.size()));
	}

	private String pickChannel(boolean fraud) {
		return fraud ? pickOne(List.of("card-not-present", "online-banking", "mobile-banking")) : pickOne(List.of("card", "card-not-present", "mobile-banking"));
	}

	private Integer randomAccountAge(CustomerProfile profile) {
		return profile.dormant() ? 240 + random.nextInt(600) : 30 + random.nextInt(900);
	}

	private Integer recentHistory(CustomerProfile profile) {
		return profile.dormant() ? 0 : random.nextInt(3);
	}

	private Instant randomHistory(CustomerProfile profile, Instant current) {
		return current.minus(random.nextInt(120), ChronoUnit.MINUTES);
	}

	private BigDecimal balanceBefore(CustomerProfile profile, BigDecimal amount) {
		return profile.typicalMonthlySpend().multiply(BigDecimal.valueOf(2.5))
				.add(normalAmount(profile.averageTransactionAmount(), 8.0, 20.0))
				.max(amount.add(BigDecimal.valueOf(50)));
	}

	private BigDecimal normalAmount(BigDecimal average, double minMultiplier, double maxMultiplier) {
		var multiplier = minMultiplier + (maxMultiplier - minMultiplier) * random.nextDouble();
		return average.multiply(BigDecimal.valueOf(multiplier)).setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal rapidBurstAmount() {
		return BigDecimal.valueOf(25 + random.nextInt(200)).setScale(2, RoundingMode.HALF_UP);
	}
}
