package com.raul.transaction_service.service;

import org.springframework.stereotype.Service;

import com.raul.transaction_service.client.FraudRuleEngineClient;
import com.raul.transaction_service.client.dto.TransactionEventRequest;
import com.raul.transaction_service.client.dto.TransactionEventResponse;
import com.raul.transaction_service.domain.TransactionEntity;
import com.raul.transaction_service.repository.TransactionRepository;

@Service
public class TransactionSubmissionService {

	private final TransactionRepository transactionRepository;
	private final FraudRuleEngineClient fraudRuleEngineClient;

	public TransactionSubmissionService(TransactionRepository transactionRepository,
			FraudRuleEngineClient fraudRuleEngineClient) {
		this.transactionRepository = transactionRepository;
		this.fraudRuleEngineClient = fraudRuleEngineClient;
	}

	public TransactionEventResponse saveAndSubmit(TransactionEventRequest request) {
		var entity = transactionRepository.save(TransactionEntity.fromRequest(request));
		try {
			var response = fraudRuleEngineClient.submitTransaction(request);
			entity.setStatus(response.status());
			entity.setMessage(response.message());
			transactionRepository.save(entity);
			return response;
		} catch (RuntimeException ex) {
			entity.setStatus("ERROR");
			entity.setMessage(ex.getMessage());
			transactionRepository.save(entity);
			throw ex;
		}
	}
}
