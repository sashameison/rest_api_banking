package com.narozhnyi.banking_app.repository;

import java.util.UUID;

import com.narozhnyi.banking_app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}
