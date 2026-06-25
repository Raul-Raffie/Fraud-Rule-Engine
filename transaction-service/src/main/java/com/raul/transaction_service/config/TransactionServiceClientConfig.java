package com.raul.transaction_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class TransactionServiceClientConfig {

	@Bean
	RestClient ruleEngineRestClient(
			@Value("${fraud.rule-engine.base-url}") String baseUrl,
			@Value("${internal.security.service-token}") String serviceToken) {
		return RestClient.builder()
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + serviceToken)
				.build();
	}
}
