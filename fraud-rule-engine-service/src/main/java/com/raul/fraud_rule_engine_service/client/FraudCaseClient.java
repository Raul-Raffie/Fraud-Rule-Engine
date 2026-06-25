package com.raul.fraud_rule_engine_service.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.raul.fraud_rule_engine_service.client.dto.FraudCaseRequest;
import com.raul.fraud_rule_engine_service.client.dto.FraudCaseResponse;

@Component
public class FraudCaseClient {

	private final RestClient fraudCaseRestClient;

	public FraudCaseClient(RestClient fraudCaseRestClient) {
		this.fraudCaseRestClient = fraudCaseRestClient;
	}

	public FraudCaseResponse createCase(FraudCaseRequest request) {
		return fraudCaseRestClient.post()
				.uri("/internal/fraud-cases")
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (httpRequest, response) -> {
					var status = response.getStatusCode();
					var body = response.getBody() == null ? "" : new String(response.getBody().readAllBytes());
					throw new IllegalStateException("Fraud case service rejected payload: " + status + " " + body);
				})
				.body(FraudCaseResponse.class);
	}
}
