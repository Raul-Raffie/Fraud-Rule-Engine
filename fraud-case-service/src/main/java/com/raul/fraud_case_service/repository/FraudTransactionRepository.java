package com.raul.fraud_case_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.raul.fraud_case_service.domain.FraudTransaction;

public interface FraudTransactionRepository extends JpaRepository<FraudTransaction, UUID> {

	Page<FraudTransaction> findAllByOrderByDetectedAtDesc(Pageable pageable);
}
