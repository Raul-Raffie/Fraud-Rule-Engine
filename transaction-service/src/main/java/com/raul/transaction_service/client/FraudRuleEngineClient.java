package com.raul.transaction_service.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.raul.transaction_service.client.dto.TransactionEventRequest;
import com.raul.transaction_service.client.dto.TransactionEventResponse;

@Component
public class FraudRuleEngineClient {

	private final RestClient ruleEngineRestClient;

	public FraudRuleEngineClient(RestClient ruleEngineRestClient) {
		this.ruleEngineRestClient = ruleEngineRestClient;
	}

	public TransactionEventResponse submitTransaction(TransactionEventRequest request) {
		return ruleEngineRestClient.post()
				.uri("/internal/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (response, context) -> {
					throw new IllegalStateException("Fraud rule engine rejected transaction");
				})
				.body(TransactionEventResponse.class);
	}
}
