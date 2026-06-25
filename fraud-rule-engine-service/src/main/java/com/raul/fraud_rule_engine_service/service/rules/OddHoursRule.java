package com.raul.fraud_rule_engine_service.service.rules;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.raul.fraud_rule_engine_service.service.FraudRule;
import com.raul.fraud_rule_engine_service.service.FraudSignal;
import com.raul.fraud_rule_engine_service.service.state.AccountState;
import com.raul.fraud_rule_engine_service.web.dto.TransactionEventRequest;

@Component
public class OddHoursRule implements FraudRule {

	private static final ZoneId LOCAL_ZONE = ZoneId.systemDefault();

	@Override
	public Optional<FraudSignal> evaluate(TransactionEventRequest event, AccountState state) {
		var hour = LocalTime.ofInstant(event.occurredAt(), LOCAL_ZONE).getHour();
		if (hour <= 4) {
			return Optional.of(new FraudSignal("odd-hours", 20, "transaction occurred between 00:00 and 04:00 local time"));
		}
		return Optional.empty();
	}
}
