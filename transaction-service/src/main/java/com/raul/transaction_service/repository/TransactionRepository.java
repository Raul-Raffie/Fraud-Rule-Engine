package com.raul.transaction_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.raul.transaction_service.domain.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

	Optional<TransactionEntity> findByTransactionId(UUID transactionId);
}
